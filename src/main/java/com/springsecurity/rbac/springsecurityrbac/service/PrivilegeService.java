package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrivilegeService {


    private PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public Optional<Privilege> findByName(String name) {
        return privilegeRepository.findByName(name);
    }

    public Privilege save(Privilege privilege) {
        Optional<Privilege> privilegeOptional = findByName(privilege.getName());
        return privilegeOptional.orElseGet(() -> privilegeRepository.save(privilege));
    }

    public List<Privilege> findAll() {
        return privilegeRepository.findAll();
    }
}
