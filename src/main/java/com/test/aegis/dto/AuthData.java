package com.test.aegis.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.test.aegis.models.entities.RoleEntities;
import com.test.aegis.models.entities.UserEntities;

import lombok.Data;

@Data
public class AuthData implements UserDetails {
    
    private Long id;
    private String fullName;
    private String username;
    @JsonIgnore
    private String password;
    private Set<RoleEntities> role;

    public AuthData(Long id, String fullName, String username, String password, Set<RoleEntities> role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static AuthData build(UserEntities userAuthEntities) {
        return new AuthData(
            userAuthEntities.getId(),
            userAuthEntities.getFullName(),
            userAuthEntities.getUsername(), 
            userAuthEntities.getPassword(),
            userAuthEntities.getRoleEntities()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.hasText(fullName)) {
            String[] splits = fullName.replaceAll(" ", "").split(",");
            for (String string : splits) {
                authorities.add(new SimpleGrantedAuthority(string));
            }

            for (RoleEntities role : this.role) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
