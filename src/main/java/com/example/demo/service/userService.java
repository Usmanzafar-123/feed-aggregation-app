package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.example.demo.dto.loginRequest;
import com.example.demo.dto.loginResponse;
import com.example.demo.dto.userRequest;
import com.example.demo.dto.userResponse;
import com.example.demo.model.Role;
import com.example.demo.model.userModel;
import com.example.demo.repository.userRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;







@Service
public class userService implements UserDetailsService{

    @Autowired
    private userRepository repository;

    @Autowired
    private jwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Counter signupCounter;
    private final Counter loginCounter;

    public userService(MeterRegistry registry) {

        this.signupCounter = Counter.builder("user.signup.count")
                .description("Total number of user signups")
                .register(registry);

        this.loginCounter = Counter.builder("user.login.count")
                .description("Total number of logins")
                .register(registry);
    }



    public userResponse createUser(userRequest user) {
        userModel userModel = new userModel();
        userModel.setUsername(user.username());
        userModel.setEmail(user.email());
        userModel.setRole(Role.USER);
        userModel.setPassword(passwordEncoder.encode(user.password()));
        userModel.setCreatedAt(System.currentTimeMillis());
        repository.save(userModel);

       return new userResponse(
            userModel.getUsername(),
            userModel.getEmail(),
            String.valueOf(userModel.getCreatedAt()),
            userModel.getRole()
        );      
    }

    
    public loginResponse login(loginRequest request) {
        String email = request.email();
        userModel user = repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found")); 

            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
        String token = jwtService.generateToken(user.getUsername());
        return new loginResponse(token);
            
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        userModel user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
    
}
