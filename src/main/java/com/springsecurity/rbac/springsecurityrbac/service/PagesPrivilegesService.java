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

    public PagesPrivileges addOrGet(PagesPrivileges pagesPrivileges) {
        String privilegeName = pagesPrivileges.getPrivilege().getName();
        String pageName = pagesPrivileges.getPage().getName();
        if (pagesPrivilegesRepository.existsByName(privilegeName, pageName)) {
            return pagesPrivilegesRepository.findByName(privilegeName, pageName);
        }
        return pagesPrivilegesRepository.save(pagesPrivileges);
    }

    public PagesPrivileges findByName(String privilegeName, String pageName) throws NoSuchElementException {
        if (pagesPrivilegesRepository.existsByName(privilegeName, pageName)) {
            return pagesPrivilegesRepository.findByName(privilegeName, pageName);
        }
        throw new NoSuchElementException("No mappings found between page " + pageName + " and privilege " + privilegeName + "!");
    }
}
