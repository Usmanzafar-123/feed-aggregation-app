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
import com.example.demo.model.followModel;
import com.example.demo.model.postModel;
import com.example.demo.repository.postRepository;

import ch.qos.logback.core.util.Duration;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import com.example.demo.repository.followRepository;
import org.springframework.data.redis.core.RedisTemplate;






@Service
public class postService {

    @Autowired
    private postRepository repository;

    @Autowired
    private followService followService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    //micrometer core object for counter and timer
    private final Counter feedReadCounter;
    private final Timer feedReadTimer;

    public postService(MeterRegistry registry) {

        this.feedReadCounter = Counter.builder("feed.read.count")
                .description("Number of times feed is read")
                .register(registry);

        this.feedReadTimer = Timer.builder("feed.read.latency")
                .description("Time taken to fetch user feed")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }


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

    @Cacheable(value = "feed", key = "#followingNames")
    public List<postResponse> getPostsByUsername(String username, int page, int size) {

        
        String cacheKey = "feed:" + username + ":page:" + page + ":size:" + size;

        // 1️⃣ Try cache
        List<postResponse> cached =
            (List<postResponse>) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return cached;
        }

         // 2️⃣ Cache miss → DB
       // 2️⃣ DB call
    Pageable pageable =
            PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<postModel> postPage =
            repository.findPostsByUsername(username, pageable);

    List<postResponse> response =
            postPage.getContent()
                    .stream()
                    .map(post -> new postResponse(
                            post.getId(),
                            post.getContent(),
                            post.getUsername(),
                            post.getCreatedAt()
                    ))
                    .toList();

            // 3️⃣ Store in Redis with TTL 3rd line
            redisTemplate.opsForValue().set(
                cacheKey,
                response, 
                java.time.Duration.ofMinutes(5)
            );
    


        return feedReadTimer.record(() -> {
            feedReadCounter.increment();

                return postPage.getContent().stream()
                .map(post -> new postResponse(post.getId(), post.getContent(), post.getUsername(), post.getCreatedAt()))
                .toList();
            
        });
    }
    
}
