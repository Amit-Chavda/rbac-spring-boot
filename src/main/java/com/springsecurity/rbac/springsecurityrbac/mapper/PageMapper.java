package com.springsecurity.rbac.springsecurityrbac.mapper;

import com.springsecurity.rbac.springsecurityrbac.dto.PageDto;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Page;
import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PageMapper {

    public static Page toPage(PageDto pageDto) {
        return new Page(pageDto.getName());
    }

    public static Collection<Page> toPages(Collection<PageDto> pageDtos) {
        return pageDtos.stream().map(PageMapper::toPage).toList();
    }

    public static PageDto toPageDto(Page page) {
        return new PageDto(page.getName());
    }

    public static Collection<PageDto> toPageDtos(Collection<Page> pages) {
        return pages.stream().map(PageMapper::toPageDto).toList();
    }

    public static Collection<PagesPrivileges> toPagesPrivileges(Page page, Collection<Privilege> privileges) {
        List<PagesPrivileges> pagesPrivilegesList1 = new ArrayList<>();
        for (Privilege privilege : privileges) {
            PagesPrivileges pagesPrivileges = new PagesPrivileges();
            pagesPrivileges.setPage(page);
            pagesPrivileges.setPrivilege(privilege);
            pagesPrivilegesList1.add(pagesPrivileges);
        }
        return pagesPrivilegesList1;
    }
}
