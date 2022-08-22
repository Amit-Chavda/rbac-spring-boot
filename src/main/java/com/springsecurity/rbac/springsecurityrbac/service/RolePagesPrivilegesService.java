package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.security.RolePagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.repository.RolePagesPrivilegesRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RolePagesPrivilegesService {

    private RolePagesPrivilegesRepository rolePagesPrivilegesRepository;

    public RolePagesPrivilegesService(RolePagesPrivilegesRepository rolePagesPrivilegesRepository) {
        this.rolePagesPrivilegesRepository = rolePagesPrivilegesRepository;
    }

    public RolePagesPrivileges add(RolePagesPrivileges rolePagesPrivileges) {
        long roleId = rolePagesPrivileges.getRole().getId();
        long pagesPrivilegesId = rolePagesPrivileges.getPagesPrivileges().getId();
        if (rolePagesPrivilegesRepository.existById(roleId, pagesPrivilegesId)) {
            return rolePagesPrivilegesRepository.findById(roleId, pagesPrivilegesId);
        }
        return rolePagesPrivilegesRepository.save(rolePagesPrivileges);
    }

    public RolePagesPrivileges addSpecialPrivileges(RolePagesPrivileges rolePagesPrivileges) {
        return rolePagesPrivilegesRepository.save(rolePagesPrivileges);
    }

    public void delete(RolePagesPrivileges rolePagesPrivileges) {
        rolePagesPrivilegesRepository.delete(rolePagesPrivileges);
    }

    public void deleteById(long id) {
        rolePagesPrivilegesRepository.deleteById(id);
    }
}
