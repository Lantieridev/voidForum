package com.voidforum.service;

import com.voidforum.dto.UserLoginDto;
import com.voidforum.dto.UserRegisterDto;
import com.voidforum.dto.UserResponseDto;
import com.voidforum.exception.ConflictException;
import com.voidforum.exception.UnauthorizedException;
import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    private final AuthService authService = new AuthService(userRepository, jwtService, passwordEncoder);

    @Test
    void register_createsTheUserWithAnEncodedPassword() {
        UserRegisterDto request = new UserRegisterDto("martin", "martin@example.com", "plaintext");
        when(userRepository.findByUsername("martin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plaintext")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("user-1");
            return u;
        });

        UserResponseDto response = authService.register(request);

        verify(userRepository).save(argThat(u ->
                u.getUsername().equals("martin")
                        && u.getEmail().equals("martin@example.com")
                        && u.getPassword().equals("hashed")
        ));
        assertThat(response.id()).isEqualTo("user-1");
        assertThat(response.username()).isEqualTo("martin");
    }

    @Test
    void register_throwsConflict_whenTheUsernameAlreadyExists() {
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(mock(User.class)));

        assertThatThrownBy(() -> authService.register(new UserRegisterDto("martin", "x@example.com", "pw")))
                .isInstanceOf(ConflictException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsATokenAndTheUser_onCorrectCredentials() {
        User user = User.builder().id("user-1").username("martin").password("hashed").createdAt(LocalDateTime.now()).build();
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plaintext", "hashed")).thenReturn(true);
        when(jwtService.generateToken("martin")).thenReturn("jwt-token");

        Map<String, Object> result = authService.login(new UserLoginDto("martin", "plaintext"));

        assertThat(result.get("token")).isEqualTo("jwt-token");
        assertThat(((UserResponseDto) result.get("user")).username()).isEqualTo("martin");
    }

    @Test
    void login_throwsUnauthorized_whenTheUsernameDoesNotExist() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new UserLoginDto("ghost", "pw")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_throwsUnauthorized_whenThePasswordIsWrong() {
        User user = User.builder().id("user-1").username("martin").password("hashed").build();
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new UserLoginDto("martin", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_usesTheExactSameErrorMessage_forUnknownUsernameAndWrongPassword() {
        // Regression test: distinguishing these two cases lets a caller
        // enumerate registered usernames by probing different logins.
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        String unknownUserMessage = catchMessage(() -> authService.login(new UserLoginDto("ghost", "pw")));

        User user = User.builder().id("user-1").username("martin").password("hashed").build();
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
        String wrongPasswordMessage = catchMessage(() -> authService.login(new UserLoginDto("martin", "wrong")));

        assertThat(unknownUserMessage).isEqualTo(wrongPasswordMessage);
    }

    private String catchMessage(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected an exception but none was thrown");
        } catch (UnauthorizedException e) {
            return e.getMessage();
        }
    }

    @Test
    void getCurrentUser_returnsTheUser_forAValidToken() {
        when(jwtService.validateToken("jwt-token")).thenReturn(true);
        when(jwtService.extractUsername("jwt-token")).thenReturn("martin");
        User user = User.builder().id("user-1").username("martin").createdAt(LocalDateTime.now()).build();
        when(userRepository.findByUsername("martin")).thenReturn(Optional.of(user));

        Map<String, Object> result = authService.getCurrentUser("jwt-token");

        assertThat(((UserResponseDto) result.get("user")).username()).isEqualTo("martin");
    }

    @Test
    void getCurrentUser_throwsUnauthorized_whenTheTokenIsInvalid() {
        when(jwtService.validateToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.getCurrentUser("bad-token"))
                .isInstanceOf(UnauthorizedException.class);
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void getCurrentUser_throwsUnauthorized_whenTheTokenIsValidButTheUserNoLongerExists() {
        when(jwtService.validateToken("jwt-token")).thenReturn(true);
        when(jwtService.extractUsername("jwt-token")).thenReturn("deleted-user");
        when(userRepository.findByUsername("deleted-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser("jwt-token"))
                .isInstanceOf(UnauthorizedException.class);
    }
}
