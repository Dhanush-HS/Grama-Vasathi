package com.example.gramavasathi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gramavasathi.data.preferences.UserFlowPreferences
import com.example.gramavasathi.navigation.AppFlow
import com.example.gramavasathi.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userFlowPreferences: UserFlowPreferences
) : ViewModel() {

    companion object {
        private const val MIN_SPLASH_MS = 1_500L
        /** Wait for persisted Firebase session to resolve (storage can lag). */
        private const val AUTH_WAIT_MS = 3_000L
    }

    /**
     * After splash: welcome/login if no session; otherwise main root from saved guest/host flow.
     * Waits for Firebase Auth’s first state emission so we don’t treat a restoring user as logged out.
     */
    suspend fun resolveNextRoute(): String = coroutineScope {
        val loggedInDeferred = async { awaitRestoredAuthSession() }
        delay(MIN_SPLASH_MS)
        val loggedIn = loggedInDeferred.await()
        if (!loggedIn) return@coroutineScope Screen.WelcomeAuth.route
        when (userFlowPreferences.appFlow.first()) {
            AppFlow.Host -> Screen.Host.route
            AppFlow.Guest -> Screen.Discover.route
        }
    }

    private suspend fun awaitRestoredAuthSession(): Boolean =
        withTimeoutOrNull(AUTH_WAIT_MS) {
            suspendCancellableCoroutine { cont ->
                val holder = arrayOfNulls<FirebaseAuth.AuthStateListener>(1)
                holder[0] = FirebaseAuth.AuthStateListener {
                    holder[0]?.let { auth.removeAuthStateListener(it) }
                    if (cont.isActive) {
                        cont.resume(auth.currentUser != null)
                    }
                }
                auth.addAuthStateListener(holder[0]!!)
                cont.invokeOnCancellation {
                    holder[0]?.let { auth.removeAuthStateListener(it) }
                }
            }
        } ?: (auth.currentUser != null)
}
