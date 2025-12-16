package com.example.pcparts.service;

import com.example.pcparts.dto.auth.LoginRequest;
import com.example.pcparts.security.JwtService;
import com.example.pcparts.security.UserPrincipal;
import com.example.pcparts.vo.auth.LoginResponse;
import com.example.pcparts.vo.auth.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        return new LoginResponse(token, principal.getUserId(), principal.getUsername(), principal.getNickname(), principal.getRoles());
    }

    public MeResponse me(UserPrincipal principal) {
        return new MeResponse(principal.getUserId(), principal.getUsername(), principal.getNickname(), principal.getRoles());
    }
}
