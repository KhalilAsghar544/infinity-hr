package com.infinityhr.auth.enums;

/**
 * Lifecycle of an account. Being locked out after failed logins is deliberately NOT a status:
 * it expires on its own, so it is stored as {@code lockedUntil} on the user.
 */
public enum UserStatus {
    /** Created but the person has not set a password / activated yet. */
    PENDING_ACTIVATION,
    ACTIVE,
    /** Switched off by an admin, e.g. when an employee leaves. Cannot log in. */
    DEACTIVATED
}
