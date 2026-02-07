package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.followRequest;
import com.example.demo.model.followModel;
import com.example.demo.repository.followRepository;

@Service
public class followService {

    @Autowired
    private followRepository repository;

    public followRequest createFollow(followRequest request) {
        followModel follow = new followModel();
        follow.setFollowerEmail(request.followerEmail());
        follow.setFolloweeEmail(request.followeeEmail());
        repository.save(follow);
        System.out.println("Follow created: " + follow.getFollowerEmail() + " -> " + follow.getFolloweeEmail());
        System.out.println("request created: " + request.followeeEmail() + " -> " + request.followerEmail());

        return new followRequest(follow.getFollowerEmail(), follow.getFolloweeEmail());
    }
    
    public followRequest unfollow(followRequest request) {
        followModel follow = repository.findByFollowerEmailAndFolloweeEmail(
            request.followerEmail(), request.followeeEmail()
        ).orElseThrow(() -> new RuntimeException("Follow relationship not found"));

        repository.delete(follow);

        return new followRequest(follow.getFollowerEmail(), follow.getFolloweeEmail());
    }

    public List<followRequest> getAllFolloweesByEmail(String email){
        List<followModel> followModels = repository.findAllByfollowerEmail(email).stream()
            .filter(follow -> follow.getFollowerEmail().equals(email))
            .toList();

        return followModels.stream().map(follow -> new followRequest(
            follow.getFollowerEmail(),
            follow.getFolloweeEmail()
        )).toList();
    }

    public List<followRequest> getAllFollowerByEmail(String email){
        List<followModel> followModels = repository.findAllByfolloweeEmail(email).stream()
            .filter(follow -> follow.getFolloweeEmail().equals(email))
            .toList();

        return followModels.stream().map(follow -> new followRequest(
            follow.getFollowerEmail(),
            follow.getFolloweeEmail()
        )).toList();
    }
    
}
