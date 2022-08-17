package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.repository.PageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PageService {

    private PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Optional<Page> findByName(String name) {
        return pageRepository.findByName(name);
    }

    public Page save(Page page) {
        Optional<Page> optionalPage = findByName(page.getName());
        return optionalPage.orElseGet(() -> pageRepository.save(page));
    }

    public List<Page> findAll() {
        return pageRepository.findAll();
    }
}
