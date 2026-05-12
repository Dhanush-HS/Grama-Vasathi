package com.example.gramavasathi.navigation

/**
 * When user picks an activity on the Farm tab, we navigate to Discover and apply this filter once.
 */
object DiscoverFilterHolder {
    @Volatile
    private var pending: String? = null

    fun setPendingFilter(filter: String) {
        pending = filter
    }

    fun consumePendingFilter(): String? {
        val v = pending
        pending = null
        return v
    }
}
