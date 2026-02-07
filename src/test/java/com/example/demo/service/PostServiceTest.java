
package com.example.demo.service;

import com.example.demo.model.postModel;
import com.example.demo.repository.postRepository;
import com.example.demo.dto.postResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    @Mock
    private postRepository repository;

    @InjectMocks
    private postService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFeedPaginated() {
        // Arrange
        postModel post1 = new postModel();
        post1.setId(1L);
        post1.setUsername("usman");
        post1.setContent("Hello");
        post1.setCreatedAt("1000");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<postModel> page = new PageImpl<>(List.of(post1), pageable, 1);

        when(repository.findByUsernameInOrderByCreatedAtDesc(List.of("usman"), pageable))
                .thenReturn(page);

        // Act
        List<postResponse> response = service.getFeedPaginated(List.of("usman"), 0, 10);

        // Assert
        assertEquals(1, response.size());
        assertEquals("Hello", response.get(0).content());
        verify(repository).findByUsernameInOrderByCreatedAtDesc(List.of("usman"), pageable);
    }

    @Test
    void testGetFeedPaginatedWithMultiplePosts() {
        // Arrange
        postModel post1 = new postModel();
        post1.setId(1L);
        post1.setUsername("user1");
        post1.setContent("Post 1");
        post1.setCreatedAt("1000");

        postModel post2 = new postModel();
        post2.setId(2L);
        post2.setUsername("user2");
        post2.setContent("Post 2");
        post2.setCreatedAt("2000");

        postModel post3 = new postModel();
        post3.setId(3L);
        post3.setUsername("user1");
        post3.setContent("Post 3");
        post3.setCreatedAt("3000");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<postModel> page = new PageImpl<>(List.of(post1, post2, post3), pageable, 3);

        when(repository.findByUsernameInOrderByCreatedAtDesc(List.of("user1", "user2"), pageable))
                .thenReturn(page);

        // Act
        List<postResponse> response = service.getFeedPaginated(List.of("user1", "user2"), 0, 10);

        // Assert
        assertEquals(3, response.size());
        assertEquals("Post 1", response.get(0).content());
        assertEquals("Post 2", response.get(1).content());
        assertEquals("Post 3", response.get(2).content());
    }

    @Test
    void testGetFeedPaginatedEmptyList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<postModel> page = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findByUsernameInOrderByCreatedAtDesc(List.of("nonexistent"), pageable))
                .thenReturn(page);

        // Act
        List<postResponse> response = service.getFeedPaginated(List.of("nonexistent"), 0, 10);

        // Assert
        assertEquals(0, response.size());
        verify(repository).findByUsernameInOrderByCreatedAtDesc(List.of("nonexistent"), pageable);
    }

    @Test
    void testGetFeedPaginatedSecondPage() {
        // Arrange
        postModel post1 = new postModel();
        post1.setId(11L);
        post1.setUsername("usman");
        post1.setContent("Post 11");
        post1.setCreatedAt("11000");

        Pageable pageable = PageRequest.of(1, 10, Sort.by("createdAt").descending());
        Page<postModel> page = new PageImpl<>(List.of(post1), pageable, 20);

        when(repository.findByUsernameInOrderByCreatedAtDesc(List.of("usman"), pageable))
                .thenReturn(page);

        // Act
        List<postResponse> response = service.getFeedPaginated(List.of("usman"), 1, 10);

        // Assert
        assertEquals(1, response.size());
        assertEquals(11L, response.get(0).id());
    }

    @Test
    void testGetFeedPaginatedMultipleFollowers() {
        // Arrange - testing with multiple following users
        postModel post1 = new postModel();
        post1.setId(1L);
        post1.setUsername("follower1");
        post1.setContent("Follower 1 post");
        post1.setCreatedAt("1000");

        postModel post2 = new postModel();
        post2.setId(2L);
        post2.setUsername("follower2");
        post2.setContent("Follower 2 post");
        post2.setCreatedAt("2000");

        postModel post3 = new postModel();
        post3.setId(3L);
        post3.setUsername("follower3");
        post3.setContent("Follower 3 post");
        post3.setCreatedAt("3000");

        List<String> followers = List.of("follower1", "follower2", "follower3");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<postModel> page = new PageImpl<>(List.of(post1, post2, post3), pageable, 3);

        when(repository.findByUsernameInOrderByCreatedAtDesc(followers, pageable))
                .thenReturn(page);

        // Act
        List<postResponse> response = service.getFeedPaginated(followers, 0, 10);

        // Assert
        assertEquals(3, response.size());
        assertTrue(response.stream().anyMatch(p -> p.username().equals("follower1")));
        assertTrue(response.stream().anyMatch(p -> p.username().equals("follower2")));
        assertTrue(response.stream().anyMatch(p -> p.username().equals("follower3")));
    }

    @Test
    void testGetFeedPaginatedVerifyRepositoryCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<postModel> page = new PageImpl<>(List.of(), pageable, 0);
        List<String> followers = List.of("user1");

        when(repository.findByUsernameInOrderByCreatedAtDesc(followers, pageable))
                .thenReturn(page);

        // Act
        service.getFeedPaginated(followers, 0, 10);

        // Assert
        verify(repository, times(1)).findByUsernameInOrderByCreatedAtDesc(followers, pageable);
        verify(repository, never()).findByUsernameInOrderByCreatedAtDesc(List.of("user2"), pageable);
    }
}

