package com.infinityhr.auth.user;

import com.infinityhr.auth.entities.User;
import com.infinityhr.auth.entities.Role;
import com.infinityhr.auth.enums.UserStatus;
import com.infinityhr.auth.repositories.RoleRepository;
import com.infinityhr.auth.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs against the local PostgreSQL database (replace = NONE), so Flyway migrations are exercised for real.
 * Every test is rolled back, leaving the database untouched.
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository users;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private EntityManager entityManager;

    @Test
    void seededRolesCarryTheirPermissions() {
        Role superAdmin = roles.findByCode("SUPER_ADMIN").orElseThrow();
        Role employee = roles.findByCode("EMPLOYEE").orElseThrow();

        assertThat(superAdmin.getPermissions()).hasSize(30);
        assertThat(employee.getPermissions())
                .extracting("code")
                .contains("LEAVE_APPLY", "EMPLOYEE_VIEW")
                .doesNotContain("EMPLOYEE_CREATE", "LEAVE_APPROVE");
    }

    @Test
    void userGetsPermissionsFromAllOfTheirRoles() {
        User user = new User("ayesha.khan", "ayesha.khan@infinityhr.com", "not-a-real-hash");
        user.assignRole(roles.findByCode("EMPLOYEE").orElseThrow());
        user.assignRole(roles.findByCode("TEAM_LEAD").orElseThrow());
        users.saveAndFlush(user);
        entityManager.clear(); // force the next read to come from the database, not the cache

        User loaded = users.findByUsernameIgnoreCase("AYESHA.KHAN").orElseThrow();

        assertThat(loaded.getStatus()).isEqualTo(UserStatus.PENDING_ACTIVATION);
        assertThat(loaded.permissionCodes())
                .contains("LEAVE_APPLY", "LEAVE_APPROVE", "PERFORMANCE_REVIEW")
                .doesNotContain("PAYROLL_PROCESS");
    }

    @Test
    void usernameIsUniqueIgnoringCase() {
        users.saveAndFlush(new User("bilal", "bilal@infinityhr.com", "hash"));

        assertThatThrownBy(() -> users.saveAndFlush(new User("BILAL", "other@infinityhr.com", "hash")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
