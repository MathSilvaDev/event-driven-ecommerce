package com.matheus.ecommerce.application.auth.service;

import com.matheus.ecommerce.application.auth.dto.request.LoginRequest;
import com.matheus.ecommerce.application.auth.dto.request.RefreshRequest;
import com.matheus.ecommerce.application.auth.dto.request.RegisterRequest;
import com.matheus.ecommerce.application.auth.dto.response.LoginResponse;
import com.matheus.ecommerce.application.auth.dto.response.RefreshResponse;
import com.matheus.ecommerce.domain.auth.entity.RefreshToken;
import com.matheus.ecommerce.domain.auth.repository.RefreshTokenRepository;
import com.matheus.ecommerce.domain.auth.entity.Role;
import com.matheus.ecommerce.domain.auth.enums.RoleName;
import com.matheus.ecommerce.domain.auth.repository.RoleRepository;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.application.auth.dto.response.TokenDTO;
import com.matheus.ecommerce.infrastructure.exception.auth.UserNotFoundException;
import com.matheus.ecommerce.infrastructure.security.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        TokenDTO accessTokenDTO =
                jwtService.generateAccessToken(user);

        TokenDTO refreshTokenDTO =
                jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken(
                refreshTokenDTO.token(),
                refreshTokenDTO.expiresAt(),
                user
        );

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse((r) -> {
                        r.setRefreshToken(
                                refreshToken.getToken(),
                                refreshToken.getExpiresAt()
                        );
                    }, () -> refreshTokenRepository.save(refreshToken));

        return new LoginResponse(
                accessTokenDTO,
                refreshTokenDTO
        );
    }

    @Transactional
    public void register(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This email is already in use");
        }

        Role role = roleRepository.findByName(RoleName.BASIC)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"));

        String passwordEncoded = passwordEncoder.encode(request.password());

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoded
        );

        user.addRoles(List.of(role));

        userRepository.save(user);
    }

    public RefreshResponse refreshToken(RefreshRequest request){
        UUID userId = jwtService.extractUserId(request.refreshToken());

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        TokenDTO newTokenAccess = jwtService.refreshToken(request.refreshToken(), user);

        return new RefreshResponse(newTokenAccess);
    }

}
