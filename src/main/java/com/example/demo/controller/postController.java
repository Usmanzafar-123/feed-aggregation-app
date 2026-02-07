package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.followRequest;
import com.example.demo.dto.postResponse;
import com.example.demo.service.postService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.demo.service.followService;

import java.security.Principal;
import java.util.List;




@RestController
@RequestMapping("/posts")
public class postController {

    @Autowired
    private postService service;

    @Autowired
    private followService followeeService;

    @PostMapping("/{username}/createPost")
    public postResponse postMethodName(@RequestBody postResponse request,@PathVariable String username) {
         String content = request.content();
        return service.createPost(content, username);
    }

    @GetMapping("/getAllPosts")
    public List<postResponse> getAllPosts() {
        return service.getAllPosts();
    }

    @GetMapping("/feed")
    public ResponseEntity<List<postResponse>> getFeed(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    
        // In a real app, you'd get 'followingNames' from your Follows table
        List<String> following = List.of("usman", "friend1"); 
        List<followRequest> followings = followeeService.getAllFolloweesByEmail(""); 
    
        return ResponseEntity.ok(service.getFeedPaginated(following, page, size));
    }  

    @GetMapping("/{username}/feeds")
    public ResponseEntity<List<postResponse>> getPostsByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
            service.getPostsByUsername(username, page, size)
        );
    }
    

    @DeleteMapping("/{postId}/deletePost")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        // Implement delete logic in service
        service.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }


    
}
