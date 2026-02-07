package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.followRequest;
import com.example.demo.service.followService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/follows")
public class followController {

    @Autowired
    private followService service;

    @PostMapping
    public ResponseEntity<followRequest> postMethodName(@RequestBody followRequest entity) {
        return ResponseEntity.ok(service.createFollow(entity));
    }

    @DeleteMapping
    public ResponseEntity<followRequest> deleteMethodName(@RequestBody followRequest entity) { 
        return ResponseEntity.ok(service.unfollow(entity));
    }

    @GetMapping("/myFollowees")
    public List<followRequest> userFollows(@RequestParam String email) {
        return service.getAllFolloweesByEmail(email);
    }

    @GetMapping("/myFollowers")
    public List<followRequest> userFollowers(@RequestParam String email) {
        return service.getAllFollowerByEmail(email);
    }
    
    

    
}
