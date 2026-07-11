const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

// Helper: Calculate Distance (Haversine)
function getDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}

const COMPATIBLE_TYPES = {
    'O_NEGATIVE': ['O_NEGATIVE', 'O_POSITIVE', 'A_NEGATIVE', 'A_POSITIVE', 'B_NEGATIVE', 'B_POSITIVE', 'AB_NEGATIVE', 'AB_POSITIVE'],
    'O_POSITIVE': ['O_POSITIVE', 'A_POSITIVE', 'B_POSITIVE', 'AB_POSITIVE'],
    'A_NEGATIVE': ['A_NEGATIVE', 'A_POSITIVE', 'AB_NEGATIVE', 'AB_POSITIVE'],
    'A_POSITIVE': ['A_POSITIVE', 'AB_POSITIVE'],
    'B_NEGATIVE': ['B_NEGATIVE', 'B_POSITIVE', 'AB_NEGATIVE', 'AB_POSITIVE'],
    'B_POSITIVE': ['B_POSITIVE', 'AB_POSITIVE'],
    'AB_NEGATIVE': ['AB_NEGATIVE', 'AB_POSITIVE'],
    'AB_POSITIVE': ['AB_POSITIVE']
};

// 1. Notify donors on new request (Geo + Type Filter)
exports.notifyDonorsOnNewRequest = functions.firestore
    .document('requests/{requestId}')
    .onCreate(async (snap, context) => {
        const requestData = snap.data();
        const { bloodType, hospitalLocation, patientName, hospitalName } = requestData;

        if (!hospitalLocation) return null;

        const donorsSnapshot = await db.collection('users')
            .where('isAvailable', '==', true)
            .get();

        const tokens = [];
        donorsSnapshot.forEach(doc => {
            const donor = doc.data();
            // Check compatibility (Recipient bloodType can receive from Donor bloodType)
            // Note: The logic here depends on how bloodType is stored.
            // If donor.bloodType can give to requestData.bloodType:
            const canDonate = COMPATIBLE_TYPES[donor.bloodType]?.includes(bloodType);

            if (canDonate && donor.location && donor.fcmToken) {
                const dist = getDistance(
                    hospitalLocation.latitude, hospitalLocation.longitude,
                    donor.location.latitude, donor.location.longitude
                );
                if (dist <= 10) {
                    tokens.push(donor.fcmToken);
                }
            }
        });

        if (tokens.length === 0) return null;

        const message = {
            notification: {
                title: 'Urgent Blood Needed Nearby!',
                body: `${patientName} needs ${bloodType} blood at ${hospitalName}.`,
            },
            data: { requestId: context.params.requestId },
            tokens: tokens
        };

        return admin.messaging().sendEachForMulticast(message);
    });

// 2. Award badges and update stats on completion
exports.awardBadgesOnCompletion = functions.firestore
    .document('requests/{requestId}')
    .onUpdate(async (change, context) => {
        const newData = change.after.data();
        const oldData = change.before.data();

        if (newData.status === 'COMPLETED' && oldData.status !== 'COMPLETED') {
            const donorId = newData.acceptedById;
            if (!donorId) return null;

            const userRef = db.collection('users').doc(donorId);
            return db.runTransaction(async (transaction) => {
                const userDoc = await transaction.get(userRef);
                if (!userDoc.exists) return;

                const userData = userDoc.data();
                const newTotal = (userData.totalDonations || 0) + 1;
                let newPoints = (userData.points || 0) + 100; // Base points for donation
                const newBadges = [...(userData.badges || [])];

                // Badge Logic
                const checkBadge = (threshold, name, desc, type) => {
                    if (newTotal === threshold) {
                        newBadges.push({
                            id: admin.firestore().collection('unused').doc().id,
                            type: type,
                            name: name,
                            description: desc,
                            awardedAt: admin.firestore.Timestamp.now()
                        });
                        newPoints += 500; // Bonus points for badge
                    }
                };

                checkBadge(1, 'First Donation', 'Saved your first life!', 'FIRST_DONATION');
                checkBadge(5, 'Life Saver', '5 successful donations', 'LIFE_SAVER');
                checkBadge(10, 'Frequent Donor', '10 successful donations', 'FREQUENT_DONOR');
                checkBadge(25, 'Elite Hero', '25 successful donations', 'ELITE_HERO');

                transaction.update(userRef, {
                    totalDonations: newTotal,
                    points: newPoints,
                    badges: newBadges,
                    isAvailable: false,
                    lastDonationDate: admin.firestore.Timestamp.now()
                });
            });
        }
        return null;
    });

// 3. Sync Leaderboard (Safe Snapshot)
exports.syncLeaderboard = functions.firestore
    .document('users/{userId}')
    .onUpdate(async (change, context) => {
        const userData = change.after.data();
        const { name, bloodType, points, totalDonations, badges } = userData;

        return db.collection('leaderboard').doc(context.params.userId).set({
            name: name || 'Anonymous',
            bloodType: bloodType || 'Unknown',
            points: points || 0,
            totalDonations: totalDonations || 0,
            badgeCount: badges ? badges.length : 0,
            lastUpdated: admin.firestore.Timestamp.now()
        });
    });

// 4. Maintenance: Reset Availability (90 days)
exports.resetAvailability = functions.pubsub.schedule('every 24 hours').onRun(async (context) => {
    const ninetyDaysAgo = admin.firestore.Timestamp.fromDate(new Date(Date.now() - 90 * 24 * 60 * 60 * 1000));
    const snapshot = await db.collection('users')
        .where('isAvailable', '==', false)
        .where('lastDonationDate', '<=', ninetyDaysAgo)
        .get();

    const batch = db.batch();
    snapshot.forEach(doc => {
        batch.update(doc.ref, { isAvailable: true });
    });
    return batch.commit();
});
