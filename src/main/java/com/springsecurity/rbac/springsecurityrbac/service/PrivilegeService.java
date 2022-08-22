package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.PrivilegeDto;
import com.springsecurity.rbac.springsecurityrbac.entity.security.Privilege;
import com.springsecurity.rbac.springsecurityrbac.mapper.PrivilegeMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.PrivilegeRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
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
        return PrivilegeMapper.toPrivilegeDto(privilegeRepository.save(PrivilegeMapper.toPrivilege(privilegeDto)));
    }

    public PrivilegeDto addOrGet(PrivilegeDto privilegeDto) {
        return PrivilegeMapper.toPrivilegeDto(privilegeRepository.findByName(privilegeDto.getName()).orElseGet(
                () -> privilegeRepository.save(PrivilegeMapper.toPrivilege(privilegeDto))
        ));
    }

    public Collection<PrivilegeDto> findAll() {
        return PrivilegeMapper.toPrivilegeDtos(privilegeRepository.findAll());
    }

    public PrivilegeDto remove(PrivilegeDto privilegeDto) throws NoSuchElementException {
        Privilege privilege = privilegeRepository.findByName(privilegeDto.getName()).orElseThrow(
                () -> new NoSuchElementException("Privilege with name " + privilegeDto.getName() + " not found")
        );
        privilegeRepository.delete(privilege);
        return PrivilegeMapper.toPrivilegeDto(privilege);
    }

}
