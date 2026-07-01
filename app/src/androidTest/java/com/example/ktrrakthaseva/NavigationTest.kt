package com.example.ktrrakthaseva

import android.Manifest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    // Rule 0: Grant permissions before the Activity starts
    @get:Rule(order = 0)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )

    // Rule 1: Hilt rule to manage dependency injection
    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    // Rule 2: Compose rule to launch the Activity
    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testOnboardingToLoginFlow() {
        // Wait for Splash screen to navigate to Onboarding
        composeTestRule.waitUntilAtLeastOneExists(hasText("Every drop counts."), timeoutMillis = 10000)
        
        // Verify Onboarding Screen content
        composeTestRule.onNodeWithText("Every drop counts.").assertExists()
        
        // Navigate to Login
        composeTestRule.onNodeWithText("GET STARTED").performClick()
        
        // Wait for and verify Login Screen
        composeTestRule.waitUntilAtLeastOneExists(hasText("LOGIN"), timeoutMillis = 5000)
        composeTestRule.onNodeWithText("LOGIN").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testLoginToRegisterNavigation() {
        // Navigate through Onboarding
        composeTestRule.waitUntilAtLeastOneExists(hasText("GET STARTED"), timeoutMillis = 10000)
        composeTestRule.onNodeWithText("GET STARTED").performClick()
        
        // Wait for Login screen and click Sign Up
        composeTestRule.waitUntilAtLeastOneExists(hasText("Sign Up"), timeoutMillis = 5000)
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // Verify Register Screen Step 1
        composeTestRule.waitUntilAtLeastOneExists(hasText("Personal Details"), timeoutMillis = 5000)
        composeTestRule.onNodeWithText("Personal Details").assertExists()
    }
}
