package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.mapper.PageMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PageRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class PageService {

    private PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public PageDto findByName(String name) throws NoSuchElementException {
        Optional<Page> optionalPage = pageRepository.findByName(name);
        return PageMapper.toPageDto(optionalPage.orElseThrow(
                () -> new NoSuchElementException("Page with name " + name + " not found")
        ));
    }

    public PageDto add(PageDto pageDto) {
        Page page = pageRepository.save(PageMapper.toPage(pageDto));
        return PageMapper.toPageDto(page);
    }

    public Collection<PageDto> findAll() {
        return PageMapper.toPageDtos(pageRepository.findAll());
    }

    public PageDto remove(PageDto pageDto) throws NoSuchElementException {
        Optional<Page> pageOptional = pageRepository.findByName(pageDto.getName());
        Page page = pageOptional.orElseThrow(() -> new NoSuchElementException("Page with name " + pageDto.getName() + " not found"));
        return PageMapper.toPageDto(page);
    }
}
