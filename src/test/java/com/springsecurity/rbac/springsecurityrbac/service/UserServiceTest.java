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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        UserDto userDto = new UserDto();
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setPassword("password");
        userDto.setEmail("test@test.com");
        userDto.setEnabled(true);

        User user = UserMapper.toUser(userDto);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setSpecialPrivileges(false);
        //user.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // Act
        UserDto actualResult = this.userService.createUser(userDto);

        // Assert
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, times(1)).save(any());

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getPassword()).isEmpty();
        assertThat(actualResult.getEmail()).isNotNull();

    }

    /**
     * Method under test: {@link UserService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setPassword("password");
        userDto.setEmail("test@test.com");
        userDto.setEnabled(true);

        UserDto expected = new UserDto();
        expected.setFirstName("firstname");
        expected.setLastName("lastname");
        expected.setEmail("test@test.com");
        expected.setPassword("");
        expected.setEnabled(true);
        expected.setSpecialPagesPrivileges(Collections.emptyList());
        expected.setRoles(Collections.emptyList());

        User user = UserMapper.toUser(userDto);
        when(userRepository.findAll()).thenReturn(List.of(user));

        //Act
        Collection<UserDto> actualFindAllResult = this.userService.findAll();

        // Assert
        verify(userRepository, times(1)).findAll();
        assertThat(actualFindAllResult)
                .isNotNull()
                .isEqualTo(List.of(expected));
    }

    /**
     * Method under test: {@link UserService#findByEmail(String)}
     */
    @Test
    void testFindByEmail() throws UsernameNotFoundException {
        // Arrange

        UserDto userDto = new UserDto();
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setEmail("test@test.com");
        userDto.setEnabled(true);
        userDto.setPassword("");
        userDto.setRoles(Collections.emptyList());
        userDto.setSpecialPagesPrivileges(Collections.emptyList());

        User user = UserMapper.toUser(userDto);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setSpecialPrivileges(false);

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        // Act
        UserDto actualResult = this.userService.findByEmail(userDto.getEmail());

        // Assert
        verify(userRepository, times(1)).findByEmail(userDto.getEmail());

        assertThat(actualResult).isEqualTo(userDto);

    }


    /**
     * Method under test: {@link UserService#findByEmail(String)}
     */

    @Test
    void testFindByEmail2() throws UsernameNotFoundException {
        // Arrange
        String username = "test@test.com";
        UsernameNotFoundException usernameNotFoundException = new UsernameNotFoundException("User with email " + username + " does not exists!");
        when(userRepository.findByEmail(username)).thenThrow(usernameNotFoundException);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> this.userService.findByEmail(username));
        verify(userRepository, times(1)).findByEmail(username);
    }

    /**
     * Method under test: {@link UserService#deleteByEmail(String)}
     */
    @Test
    void testDeleteByEmail() throws UsernameNotFoundException {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setEmail("test@test.com");
        userDto.setEnabled(true);
        userDto.setPassword("");
        userDto.setRoles(Collections.emptyList());
        userDto.setSpecialPagesPrivileges(Collections.emptyList());

        User user = new User();
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setSpecialPrivileges(false);


        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        // Act
        UserDto actualDeleteByEmailResult = this.userService.deleteByEmail(userDto.getEmail());

        // Assert
        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
        verify(userRepository, times(1)).delete(user);

        assertThat(actualDeleteByEmailResult)
                .isNotNull()
                .isEqualTo(userDto);
    }
}

