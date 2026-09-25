-- RBAC: a user has roles, a role grants permissions.
-- Application code checks permissions only, never role names.

CREATE TABLE permissions (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(100) NOT NULL UNIQUE,   -- e.g. EMPLOYEE_CREATE
    module      VARCHAR(50)  NOT NULL,          -- e.g. EMPLOYEE, LEAVE, PAYROLL
    description VARCHAR(255)
);

CREATE TABLE roles (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(50)  NOT NULL UNIQUE,   -- e.g. HR_ADMIN
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    system_role BOOLEAN      NOT NULL DEFAULT FALSE,  -- seeded roles that must not be deleted
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE role_permissions (
    role_id       UUID NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE users (
    id                    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    username              VARCHAR(100) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    password_hash         VARCHAR(100) NOT NULL,
    employee_id           UUID,                    -- the person in employee-service; no FK across databases
    status                VARCHAR(30)  NOT NULL,   -- PENDING_ACTIVATION, ACTIVE, DEACTIVATED
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    locked_until          TIMESTAMPTZ,             -- lockout is temporary, so it is a time, not a status
    last_login_at         TIMESTAMPTZ,
    password_changed_at   TIMESTAMPTZ,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version               BIGINT       NOT NULL DEFAULT 0   -- optimistic locking
);

-- Case-insensitive uniqueness: "Khalil" and "khalil" are the same login.
CREATE UNIQUE INDEX ux_users_username ON users (lower(username));
CREATE UNIQUE INDEX ux_users_email ON users (lower(email));
CREATE UNIQUE INDEX ux_users_employee_id ON users (employee_id) WHERE employee_id IS NOT NULL;

CREATE TABLE user_roles (
    user_id     UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id     UUID        NOT NULL REFERENCES roles (id) ON DELETE RESTRICT,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, role_id)
);
