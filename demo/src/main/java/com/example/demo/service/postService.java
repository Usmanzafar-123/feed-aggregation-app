package com.example.demo.service;

// import org.hibernate.query.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Pageable;
// import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Sort;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dto.postResponse;
import com.example.demo.model.postModel;
import com.example.demo.repository.postRepository;
import java.util.List;



@Service
public class postService {

    @Autowired
    private postRepository repository;

    public postResponse createPost(String content, String username) {
        postModel postModel = new postModel();
        postModel.setContent(content);
        postModel.setUsername(username);
        postModel.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        repository.save(postModel);

       return new postResponse(
            postModel.getId(),
            postModel.getContent(),
            postModel.getUsername(),
            postModel.getCreatedAt()
        );      
    }

    public List<postResponse> getAllPosts() {
        List<postModel> postModel = repository.findAll();


        return postModel.stream().map(post -> new postResponse(
            post.getId(),
            post.getContent(),
            post.getUsername(),
            post.getCreatedAt()
        )).toList();
    }


    public List<postResponse> getFeedPaginated(List<String> followingNames, int page, int size) {
        // PageRequest starts at 0
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    
        Page<postModel> postPage = repository.findByUsernameInOrderByCreatedAtDesc(followingNames, pageable);
    
        return postPage.getContent().stream()
                    .map(post -> new postResponse(post.getId(), post.getContent(), post.getUsername(), post.getCreatedAt()))
                    .toList();
    }

    public void deletePost(Long postId) {
        repository.deleteById(postId);
    }

    
}
