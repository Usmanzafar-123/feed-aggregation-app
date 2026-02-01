package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.loginRequest;
import com.example.demo.dto.loginResponse;
import com.example.demo.dto.userRequest;
import com.example.demo.dto.userResponse;
import com.example.demo.service.userService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;



@RestController
@RequestMapping("/users")
public class userController {

    @Autowired
    private userService service;

    @PostMapping("/signup")
    public ResponseEntity<userResponse> postMethodName(@RequestBody userRequest user) {
       userResponse userresponse =  service.createUser(user);
       return ResponseEntity.ok(userresponse);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> postMethodName(@RequestBody loginRequest request) {
        try{
            return ResponseEntity.ok(service.login(request));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
        }   
        
    }
    
}
