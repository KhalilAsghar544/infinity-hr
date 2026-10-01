package com.infinityhr.auth.security;

import com.infinityhr.auth.entities.User;
import com.infinityhr.auth.enums.UserStatus;
import lombok.Getter;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final UUID employeeId;
    private final String username;
    private final String password;
    private final Set<String> permissions;
    private final boolean active;
    private final boolean locked;


    private AuthenticatedUser(User user, Instant now){
        this.id = user.getId();
        this.employeeId = user.getEmployeeId();
        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.permissions = user.permissionCodes();
        this.active = user.getStatus() == UserStatus.ACTIVE;
        this.locked = user.getLockedUntil() != null && user.getLockedUntil().isAfter(now);
    }

    public static AuthenticatedUser from(User user, Instant now){
        return new AuthenticatedUser(user, now);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public boolean isEnabled(){
        return active;
    }

    @Override
    public boolean isAccountNonLocked(){
        return !locked;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
