package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PrivilegeService {


    private PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public PrivilegeDto findByName(String name) {
        Optional<Privilege> privilegeOptional = privilegeRepository.findByName(name);
        return PrivilegeMapper.toPrivilegeDto(privilegeOptional.orElseThrow(
                () -> new NoSuchElementException("Privilege with name " + name + " not found")
        ));

    }

    public PrivilegeDto add(PrivilegeDto privilegeDto) {
        Privilege privilege = privilegeRepository.save(PrivilegeMapper.toPrivilege(privilegeDto));
        return PrivilegeMapper.toPrivilegeDto(privilege);
    }

    public Collection<PrivilegeDto> findAll() {
        return PrivilegeMapper.toPrivilegeDtos(privilegeRepository.findAll());
    }

    public PrivilegeDto remove(PrivilegeDto privilegeDto) throws NoSuchElementException {
        Optional<Privilege> privilegeOptional = privilegeRepository.findByName(privilegeDto.getName());
        return PrivilegeMapper.toPrivilegeDto(privilegeOptional.orElseThrow(
                () -> new NoSuchElementException("Page with name " + privilegeDto.getName() + " not found")));
    }


}
