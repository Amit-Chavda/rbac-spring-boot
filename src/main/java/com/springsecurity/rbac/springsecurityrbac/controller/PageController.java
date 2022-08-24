package com.springsecurity.rbac.springsecurityrbac.controller;


import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.service.PageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/page")
public class PageController {
    private PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/findAll")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public Collection<PageDto> findAllPages() {
        return pageService.findAll();
    }

    @GetMapping("/findByName")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public PageDto findPageByName(@RequestParam String name) {
        try {
            return pageService.findByName(name);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/create")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public PageDto createPage(@RequestBody PageDto pageDto) {
        return pageService.add(pageDto);
    }

    @DeleteMapping("/remove")
    @PreAuthorize(value = "@roleChecker.check(authentication)")
    public PageDto removePage(@RequestBody PageDto pageDto) {
        try {
            return pageService.remove(pageDto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
