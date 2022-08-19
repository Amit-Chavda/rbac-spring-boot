package com.springsecurity.rbac.springsecurityrbac.entity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @OneToMany(mappedBy = "privilege", cascade = {CascadeType.MERGE})
    private Set<PagesPrivileges> pagesPrivileges;

    public Privilege(String name) {
        this.name = name;
    }
}
