package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.UserDto;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.exception.UserAlreadyExistException;
import com.springsecurity.rbac.springsecurityrbac.mapper.UserMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto createUser(UserDto userDto) throws UserAlreadyExistException {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException(UserAlreadyExistException.class.getName(),
                    "User with " + userDto.getEmail() + " already exist!", LocalDateTime.now());
        }
        User user = UserMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return UserMapper.toUserDto(save(user));

    }

    public Collection<UserDto> findAll() {
        return UserMapper.toUserDtos(userRepository.findAll());
    }

    public User findByEmail(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + username + " does not exists!");
        }
        return optionalUser.get();
    }

    public UserDto deleteByEmail(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        userRepository.delete(user);
        return UserMapper.toUserDto(user);
    }
}
