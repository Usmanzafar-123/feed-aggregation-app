package com.example.demo.repository;

import com.example.demo.model.userModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



@Repository
public interface userRepository extends JpaRepository<userModel, Long> {

    Optional<userModel> findByEmail(String email);
    
}

