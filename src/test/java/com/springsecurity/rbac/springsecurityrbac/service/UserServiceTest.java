package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.dto.UserDto;
import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.exception.UserAlreadyExistException;
import com.springsecurity.rbac.springsecurityrbac.mapper.UserMapper;
import com.springsecurity.rbac.springsecurityrbac.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    /**
     * Method under test: {@link UserService#createUser(UserDto)}
     */
    @Test
    void testCreateUser() throws UserAlreadyExistException {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setPassword("password");
        userDto.setEmail("test@test.com");
        userDto.setEnabled(true);

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // Act // Assert
        assertThrows(UserAlreadyExistException.class, () -> this.userService.createUser(userDto));

        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());

    }

    /**
     * Method under test: {@link UserService#createUser(UserDto)}
     */
    @Test
    void testCreateUser2() throws UserAlreadyExistException {
        // Arrange
/*
        UserDto userDto = new UserDto();
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setPassword("password");
        userDto.setEmail("test@test.com");
        userDto.setEnabled(true);


        User user = UserMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode("password"));
        user.setSpecialPrivileges(false);
        user.setCreatedAt(LocalDateTime.now());
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        UserDto actualResult = this.userService.createUser(userDto);

        // Assert
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, times(1)).save(user);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getPassword()).isNull();
        assertThat(actualResult.getCreatedAt()).isNotNull();
        assertThat(actualResult.getEmail()).isNotNull();

*/

    }

    /**
     * Method under test: {@link UserService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange and Act
        // TODO: Populate arranged inputs
        Collection<UserDto> actualFindAllResult = this.userService.findAll();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link UserService#findByEmail(String)}
     */
    @Test
    void testFindByEmail() throws UsernameNotFoundException {
        // Arrange
        // TODO: Populate arranged inputs
        String username = "";

        // Act
        UserDto actualFindByEmailResult = this.userService.findByEmail(username);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link UserService#deleteByEmail(String)}
     */
    @Test
    void testDeleteByEmail() throws UsernameNotFoundException {
        // Arrange
        // TODO: Populate arranged inputs
        String email = "";

        // Act
        UserDto actualDeleteByEmailResult = this.userService.deleteByEmail(email);

        // Assert
        // TODO: Add assertions on result
    }
}

