package com.sprinthon.focusclock.domain.restriction

/**
 * Clean architectural abstraction for future app-blocking and distraction management capabilities.
 * Safe no-op implementation is used for the MVP.
 */
interface RestrictionEngine {
    fun prepare()
    fun startFocus()
    fun endFocus()
    fun setAllowedApps(packages: Set<String>)
    fun setBlockedApps(packages: Set<String>)
    fun isAppAllowed(packageName: String): Boolean
}

class NoOpRestrictionEngine : RestrictionEngine {
    private val allowed = mutableSetOf<String>()
    private val blocked = mutableSetOf<String>()

    override fun prepare() {
        // No-op for MVP
    }

    override fun startFocus() {
        // No-op for MVP
    }

    override fun endFocus() {
        // No-op for MVP
    }

    override fun setAllowedApps(packages: Set<String>) {
        allowed.clear()
        allowed.addAll(packages)
    }

    override fun setBlockedApps(packages: Set<String>) {
        blocked.clear()
        blocked.addAll(packages)
    }

    override fun isAppAllowed(packageName: String): Boolean {
        return true
    }
}
