package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagesPrivilegesService {
    private PagesPrivilegesRepository pagesPrivilegesRepository;

    public PagesPrivilegesService(PagesPrivilegesRepository pagesPrivilegesRepository) {
        this.pagesPrivilegesRepository = pagesPrivilegesRepository;
    }

    public Optional<PagesPrivileges> alreadyExists(PagesPrivileges pagesPrivileges) {
        return pagesPrivilegesRepository.alreadyExists(pagesPrivileges.getPrivilege().getId(), pagesPrivileges.getPage().getId());

    }

    public PagesPrivileges add(PagesPrivileges pagesPrivileges) {
        Optional<PagesPrivileges> pagesPrivilegesOptional = pagesPrivilegesRepository.alreadyExists(
                pagesPrivileges.getPrivilege().getId(),
                pagesPrivileges.getPage().getId()
        );
        return pagesPrivilegesOptional.orElseGet(() -> pagesPrivilegesRepository.save(pagesPrivileges));
    }

    public PagesPrivileges findByName(PagesPrivileges pagesPrivileges) {
        String pageName = pagesPrivileges.getPrivilege().getName();
        String privilegeName = pagesPrivileges.getPage().getName();
        if (pagesPrivilegesRepository.existByName(privilegeName, pageName)) {
            return pagesPrivilegesRepository.findByName(privilegeName, pageName);
        }
        return pagesPrivilegesRepository.save(pagesPrivileges);
    }
}
