package com.springsecurity.rbac.springsecurityrbac.service;

import com.springsecurity.rbac.springsecurityrbac.entity.User;
import com.springsecurity.rbac.springsecurityrbac.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * Method under test: {@link UserDetailsServiceImpl#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername() throws UsernameNotFoundException {
        // Arrange
        String username = "test@test.com";
        User user = new User();
        user.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        user.setEmail(username);
        user.setEnabled(true);
        user.setFirstName("Tony");
        user.setId(123L);
        user.setLastName("Stark");
        user.setPassword("password");
        user.setRolePagesPrivileges(new ArrayList<>());
        user.setRoles(new ArrayList<>());
        user.setSpecialPrivileges(true);
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findByEmail((String) any())).thenReturn(ofResult);

        // Act and Assert
        assertTrue(userDetailsServiceImpl.loadUserByUsername(username).isEnabled());
        verify(userRepository, times(1)).findByEmail(username);
    }

    /**
     * Method under test: {@link UserDetailsServiceImpl#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername2() throws UsernameNotFoundException {
        // Arrange
        String username = "test@test.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsServiceImpl.loadUserByUsername(username));
        verify(userRepository, times(1)).findByEmail(username);
    }
}

