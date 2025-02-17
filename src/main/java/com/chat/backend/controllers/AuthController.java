package com.chat.backend.controllers;

import com.chat.backend.dto.requests.UserRequest;
import com.chat.backend.enums.TokenType;
import com.chat.backend.dto.requests.LoginRequest;
import com.chat.backend.services.JwtService;
import com.chat.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    ));

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("access_token", jwtService
                    .generateToken(loginRequest.email(), TokenType.ACCESS_TOKEN));
            responseMap.put("user_token",userService.getUserByEmail(loginRequest.email()).getToken());

            return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.OK);
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<List<String>>(List.of("Invalid Credintials"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request){
        userService.saveUser(request);
        return new ResponseEntity<>("User Created", HttpStatus.CREATED);
    }

    @GetMapping("/admin/verify")
    public ResponseEntity<?> isAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity("Error: You are unauthorized",HttpStatus.UNAUTHORIZED);
        }

        // Extract roles from the authentication object
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return new ResponseEntity("success",HttpStatus.OK);
        } else {
            return new ResponseEntity("Error: You are unauthorized",HttpStatus.UNAUTHORIZED);
        }
    }

}
