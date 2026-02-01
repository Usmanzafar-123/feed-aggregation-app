package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.example.demo.dto.loginRequest;
import com.example.demo.dto.loginResponse;
import com.example.demo.dto.userRequest;
import com.example.demo.dto.userResponse;
import com.example.demo.model.userModel;
import com.example.demo.repository.userRepository;
import org.springframework.security.crypto.password.PasswordEncoder;





@Service
public class userService {

    @Autowired
    private userRepository repository;

    @Autowired
    private jwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public userResponse createUser(userRequest user) {
        userModel userModel = new userModel();
        userModel.setUsername(user.username());
        userModel.setEmail(user.email());
        userModel.setPassword(passwordEncoder.encode(user.password()));
        userModel.setCreatedAt(System.currentTimeMillis());
        repository.save(userModel);

       return new userResponse(
            userModel.getUsername(),
            userModel.getEmail(),
            String.valueOf(userModel.getCreatedAt())
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


    
}
