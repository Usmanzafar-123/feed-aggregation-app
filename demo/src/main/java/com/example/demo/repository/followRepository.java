package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.dto.followRequest;
import com.example.demo.model.followModel;

public interface followRepository extends JpaRepository<followModel, Long> {

    Optional<followModel> findByFollowerEmailAndFolloweeEmail(String followerEmail, String followeeEmail);

    List<followModel> findAllByfollowerEmail(String email);

    List<followModel> findAllByfolloweeEmail(String email);
    
}
