package com.springsecurity.rbac.springsecurityrbac.mapper;

import com.springsecurity.rbac.springsecurityrbac.dto.PagesPrivilegesDto;
import com.springsecurity.rbac.springsecurityrbac.dto.UserDto;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;

import java.util.Collection;
import java.util.Collections;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setEnabled(user.isEnabled());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoles(RoleMapper.toRoleDtos(user.getRoles()));

        if (user.isSpecialPrivileges()) {
            Collection<PagesPrivilegesDto> pagesPrivilegesDtos =
                    user.getRolePagesPrivileges().stream()
                            .map(rolePagesPrivileges -> {
                                PagesPrivileges pagesPrivileges = rolePagesPrivileges.getPagesPrivileges();
                                PagesPrivilegesDto pagesPrivilegesDto = new PagesPrivilegesDto();
                                pagesPrivilegesDto.setPageDto(PageMapper.toPageDto(pagesPrivileges.getPage()));
                                pagesPrivilegesDto.setPrivilegeDto(PrivilegeMapper.toPrivilegeDto(pagesPrivileges.getPrivilege()));
                                return pagesPrivilegesDto;
                            }).toList();

            userDto.setSpecialPagesPrivileges(pagesPrivilegesDtos);
        } else {
            userDto.setSpecialPagesPrivileges(Collections.emptyList());
        }
        userDto.setPassword("");
        return userDto;
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setEnabled(userDto.isEnabled());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        return user;
    }

    public static Collection<UserDto> toUserDtos(Collection<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public static Collection<User> toUsers(Collection<UserDto> userDtoss) {
        return userDtoss.stream()
                .map(UserMapper::toUser)
                .toList();
    }
}
