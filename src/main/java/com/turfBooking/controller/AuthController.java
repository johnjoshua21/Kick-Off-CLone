package com.turfBooking.controller;

import com.turfBooking.dto.JwtRequest;
import com.turfBooking.dto.JwtResponse;
import com.turfBooking.dto.UserRegistrationDto;
import com.turfBooking.entity.User;
import com.turfBooking.enums.UserRole;
import com.turfBooking.security.CustomUserDetails;
import com.turfBooking.service.interfaces.UserService;
import com.turfBooking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getPhone(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getPhone());
        final CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        final String token = jwtTokenUtil.generateToken(userDetails, customUserDetails.getRole());

        return ResponseEntity.ok(new JwtResponse(token, customUserDetails.getRole(), customUserDetails.getUserId()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            // Validate registration type
            if (!"BOOK_SPORTS".equals(registrationDto.getRegistrationType()) &&
                    !"OWN_TURF".equals(registrationDto.getRegistrationType())) {
                return ResponseEntity.badRequest().body("Invalid registration type");
            }

            // Check if user already exists using your UserService
            if (userService.existsByPhone(registrationDto.getPhone())) {
                return ResponseEntity.badRequest().body("Phone number already registered");
            }

            // Create user based on registration type
            User user = new User();
            user.setName(registrationDto.getName());
            user.setPhone(registrationDto.getPhone());
            user.setPassword(registrationDto.getPassword());

            if ("BOOK_SPORTS".equals(registrationDto.getRegistrationType())) {
                user.setRole(UserRole.USER);
            } else {
                user.setRole(UserRole.TURF_OWNER);
            }

            // Use your UserService to create user with encrypted password
            User savedUser = userService.createUserEntity(user);

            // Generate token for immediate login after registration
            final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getPhone());
            final CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            final String token = jwtTokenUtil.generateToken(userDetails, customUserDetails.getRole());

            return ResponseEntity.ok(new JwtResponse(token, customUserDetails.getRole(), customUserDetails.getUserId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserRegistrationDto registrationDto) {
        try {
            // Check if user already exists
            if (userService.existsByPhone(registrationDto.getPhone())) {
                return ResponseEntity.badRequest().body("Phone number already registered");
            }

            // Validate required fields
            if (registrationDto.getName() == null || registrationDto.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }
            if (registrationDto.getPhone() == null || registrationDto.getPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Phone is required");
            }
            if (registrationDto.getPassword() == null || registrationDto.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }

            // Create admin user
            User user = new User();
            user.setName(registrationDto.getName().trim());
            user.setPhone(registrationDto.getPhone().trim());
            user.setPassword(registrationDto.getPassword());
            user.setRole(UserRole.ADMIN);  // Set as ADMIN

            // Create user with encrypted password
            User savedUser = userService.createUserEntity(user);

            // Generate token for immediate login after registration
            final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getPhone());
            final CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            final String token = jwtTokenUtil.generateToken(userDetails, customUserDetails.getRole());

            return ResponseEntity.ok(new JwtResponse(token, customUserDetails.getRole(), customUserDetails.getUserId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Admin registration failed: " + e.getMessage());
        }
    }

    private void authenticate(String phone, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(phone, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}