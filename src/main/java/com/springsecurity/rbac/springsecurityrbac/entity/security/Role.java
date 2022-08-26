package com.springsecurity.rbac.springsecurityrbac.entity.security;

import com.springsecurity.rbac.springsecurityrbac.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private String name;

    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<User> users;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role", cascade = {CascadeType.ALL})
    private Collection<RolePagesPrivileges> rolePagesPrivileges;

    public Role(String name) {
        this.name = name;
    }

}
