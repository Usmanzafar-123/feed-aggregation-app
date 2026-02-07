package com.example.demo.dto;
import com.example.demo.model.Role;



public record userResponse(String username, String email, String createdAt, Role role) {
} 