package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.repository.PagesPrivilegesRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PagesPrivilegesService {
    private PagesPrivilegesRepository pagesPrivilegesRepository;

    public PagesPrivilegesService(PagesPrivilegesRepository pagesPrivilegesRepository) {
        this.pagesPrivilegesRepository = pagesPrivilegesRepository;
    }

    public PagesPrivileges add(PagesPrivileges pagesPrivileges) {
        String pageName = pagesPrivileges.getPrivilege().getName();
        String privilegeName = pagesPrivileges.getPage().getName();
        if (pagesPrivilegesRepository.existsByName(privilegeName, pageName)) {
            return pagesPrivilegesRepository.findByName(privilegeName, pageName);
        }
        return pagesPrivilegesRepository.save(pagesPrivileges);
    }

    public PagesPrivileges findByName(PagesPrivileges pagesPrivileges) throws NoSuchElementException {
        String pageName = pagesPrivileges.getPrivilege().getName();
        String privilegeName = pagesPrivileges.getPage().getName();
        if (pagesPrivilegesRepository.existsByName(privilegeName, pageName)) {
            return pagesPrivilegesRepository.findByName(privilegeName, pageName);
        }
        throw new NoSuchElementException("No mappings found!");
    }
}
