package com.example.gramavasathi.demo

import com.example.gramavasathi.navigation.AppFlow

/**
 * Demo Firebase Auth emails/passwords for QA. On **Sign In**, if these exact credentials are used
 * and the account does not exist yet, the app registers them automatically (see AuthViewModel).
 * You can still create users manually in Firebase Console if you prefer.
 *
 * Seeded Firestore stay IDs are stable only after a fresh seed — see [DemoCredentials] comments
 * in app startup logs when the `stays` collection was empty.
 *
 * **Chosen demo listing (1 of 4 seeds):** Patil Heritage Homestay — use [DEMO_STAY_ID] in URLs / booking.
 */
object DemoCredentials {

    const val GUEST_EMAIL = "guest.demo@gramavasathi.app"
    const val GUEST_PASSWORD = "GuestDemo#2026"

    const val HOST_EMAIL = "host.patil@gramavasathi.app"
    const val HOST_PASSWORD = "HostDemo#2026"

    /** Patil Heritage Homestay — Dharwad (seeded when DB was empty). */
    const val DEMO_STAY_ID = "stay_patil_dharwad"
    const val DEMO_STAY_NAME = "Patil Heritage Homestay"

    fun emailFor(flow: AppFlow): String = when (flow) {
        AppFlow.Guest -> GUEST_EMAIL
        AppFlow.Host -> HOST_EMAIL
    }

    fun passwordFor(flow: AppFlow): String = when (flow) {
        AppFlow.Guest -> GUEST_PASSWORD
        AppFlow.Host -> HOST_PASSWORD
    }

    /** True when email/password match the built-in demo pair (case-insensitive email). */
    fun matchesKnownDemo(email: String, password: String): Boolean {
        val e = email.trim().lowercase()
        return (e == GUEST_EMAIL.lowercase() && password == GUEST_PASSWORD) ||
            (e == HOST_EMAIL.lowercase() && password == HOST_PASSWORD)
    }

    fun displayNameForDemoEmail(email: String): String {
        val e = email.trim().lowercase()
        return when (e) {
            GUEST_EMAIL.lowercase() -> "Demo Guest"
            HOST_EMAIL.lowercase() -> "Demo Host"
            else -> "Demo User"
        }
    }
}
