package com.springsecurity.rbac.springsecurityrbac.entity;

import com.springsecurity.rbac.springsecurityrbac.entity.security.Role;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

  /*  public void setRoles(Collection<Role> roles) {
        for (Role role : roles) {
            role.setUsers(List.of(this));
        }
        this.roles = roles;
    }*/
}

