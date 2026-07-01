const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// 1. Notify donors when a new request is posted
exports.notifyDonorsOnNewRequest = functions.firestore
    .document('requests/{requestId}')
    .onCreate(async (snap, context) => {
        const newValue = snap.data();
        const bloodType = newValue.bloodType;

        const payload = {
            notification: {
                title: 'Urgent Blood Needed!',
                body: `${newValue.patientName} needs ${bloodType} blood at ${newValue.hospitalName}.`,
                clickAction: 'FLUTTER_NOTIFICATION_CLICK',
            },
            data: {
                requestId: context.params.requestId,
                type: 'NEW_REQUEST'
            }
        };

        // In production, you would query users by location and blood type match
        return admin.messaging().sendToTopic('all_donors', payload);
    });

// 2. Award badges on donation completion
exports.awardBadges = functions.firestore
    .document('requests/{requestId}')
    .onUpdate(async (change, context) => {
        const newData = change.after.data();
        const oldData = change.before.data();

        if (newData.status === 'COMPLETED' && oldData.status !== 'COMPLETED') {
            // Logic to award badges to donors who participated
            console.log('Request completed. Awarding points/badges...');
        }
    });

// 3. 90-day reset for donor availability
exports.resetAvailability = functions.pubsub.schedule('every 24 hours').onRun(async (context) => {
    const ninetyDaysAgo = admin.firestore.Timestamp.fromDate(new Date(Date.now() - 90 * 24 * 60 * 60 * 1000));
    const snapshot = await admin.firestore().collection('users')
        .where('isAvailable', '==', false)
        .where('lastDonationDate', '<=', ninetyDaysAgo)
        .get();

    const batch = admin.firestore().batch();
    snapshot.forEach(doc => {
        batch.update(doc.ref, { isAvailable: true });
    });
    return batch.commit();
});

// 4. Close stale requests
exports.closeStaleRequests = functions.pubsub.schedule('every 12 hours').onRun(async (context) => {
    const twoDaysAgo = admin.firestore.Timestamp.fromDate(new Date(Date.now() - 48 * 60 * 60 * 1000));
    const snapshot = await admin.firestore().collection('requests')
        .where('status', '==', 'OPEN')
        .where('createdAt', '<=', twoDaysAgo)
        .get();

    const batch = admin.firestore().batch();
    snapshot.forEach(doc => {
        batch.update(doc.ref, { status: 'CANCELLED' });
    });
    return batch.commit();
});

// 5. Sync Leaderboard
exports.syncLeaderboard = functions.firestore
    .document('users/{userId}')
    .onUpdate(async (change, context) => {
        // Logic to recalculate ranks if necessary
    });
