package com.example.demo.repository;

import com.example.demo.model.postModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostRepositoryTest {

    @Mock
    private postRepository repository;

    public PostRepositoryTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindPostsByUsernameOrderByCreatedAtDesc() {
        // Arrange
        postModel post1 = new postModel();
        post1.setId(1L);
        post1.setUsername("usman");
        post1.setContent("First post");
        post1.setCreatedAt("1000");

        postModel post2 = new postModel();
        post2.setId(2L);
        post2.setUsername("usman");
        post2.setContent("Second post");
        post2.setCreatedAt("2000");

        Page<postModel> page = new PageImpl<>(List.of(post1, post2));

        when(repository.findPostsByUsername(
                "usman",
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        )).thenReturn(page);

        // Act
        Page<postModel> result = repository.findPostsByUsername(
                "usman",
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        );

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("First post", result.getContent().get(0).getContent());
        assertEquals("Second post", result.getContent().get(1).getContent());
    }

    @Test
    void testFindPostsByUsernameEmptyResult() {
        // Arrange
        Page<postModel> emptyPage = new PageImpl<>(List.of());

        when(repository.findPostsByUsername(
                "nonexistent",
                PageRequest.of(0, 10)
        )).thenReturn(emptyPage);

        // Act
        Page<postModel> result = repository.findPostsByUsername(
                "nonexistent",
                PageRequest.of(0, 10)
        );

        // Assert
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindPostsByUsernamePagination() {
        // Arrange - simulate 15 posts across pages
        postModel[] posts = new postModel[15];
        for (int i = 0; i < 15; i++) {
            postModel post = new postModel();
            post.setId((long) i + 1);
            post.setUsername("usman");
            post.setContent("Post " + (i + 1));
            post.setCreatedAt(String.valueOf((i + 1) * 1000));
            posts[i] = post;
        }

        Page<postModel> firstPage = new PageImpl<>(List.of(posts[0], posts[1], posts[2], posts[3], posts[4], 
                                                           posts[5], posts[6], posts[7], posts[8], posts[9]), 
                                                   PageRequest.of(0, 10), 15);
        Page<postModel> secondPage = new PageImpl<>(List.of(posts[10], posts[11], posts[12], posts[13], posts[14]),
                                                    PageRequest.of(1, 10), 15);

        when(repository.findPostsByUsername("usman", PageRequest.of(0, 10))).thenReturn(firstPage);
        when(repository.findPostsByUsername("usman", PageRequest.of(1, 10))).thenReturn(secondPage);

        // Act & Assert
        Page<postModel> actualFirstPage = repository.findPostsByUsername("usman", PageRequest.of(0, 10));
        assertEquals(10, actualFirstPage.getContent().size());
        assertEquals(15, actualFirstPage.getTotalElements());
        assertTrue(actualFirstPage.hasNext());

        Page<postModel> actualSecondPage = repository.findPostsByUsername("usman", PageRequest.of(1, 10));
        assertEquals(5, actualSecondPage.getContent().size());
        assertTrue(actualSecondPage.hasPrevious());
    }

    @Test
    void testFindPostsByUsernameMultipleUsers() {
        // Arrange
        postModel post1 = new postModel();
        post1.setId(1L);
        post1.setUsername("user1");
        post1.setContent("User1 post");
        post1.setCreatedAt("1000");

        postModel post2 = new postModel();
        post2.setId(2L);
        post2.setUsername("user2");
        post2.setContent("User2 post");
        post2.setCreatedAt("2000");

        Page<postModel> user1Page = new PageImpl<>(List.of(post1));
        Page<postModel> user2Page = new PageImpl<>(List.of(post2));

        when(repository.findPostsByUsername("user1", PageRequest.of(0, 10))).thenReturn(user1Page);
        when(repository.findPostsByUsername("user2", PageRequest.of(0, 10))).thenReturn(user2Page);

        // Act
        Page<postModel> user1Posts = repository.findPostsByUsername("user1", PageRequest.of(0, 10));
        Page<postModel> user2Posts = repository.findPostsByUsername("user2", PageRequest.of(0, 10));

        // Assert
        assertEquals(1, user1Posts.getContent().size());
        assertEquals(1, user2Posts.getContent().size());
        assertEquals("User1 post", user1Posts.getContent().get(0).getContent());
        assertEquals("User2 post", user2Posts.getContent().get(0).getContent());
    }

    @Test
    void testFindPostsById() {
        // Arrange
        postModel post = new postModel();
        post.setId(1L);
        post.setUsername("usman");
        post.setContent("Test post");
        post.setCreatedAt("1000");

        when(repository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        Optional<postModel> foundPost = repository.findById(1L);

        // Assert
        assertTrue(foundPost.isPresent());
        assertEquals("Test post", foundPost.get().getContent());
        assertEquals("usman", foundPost.get().getUsername());
    }

    @Test
    void testSavePost() {
        // Arrange
        postModel post = new postModel();
        post.setUsername("usman");
        post.setContent("New post");
        post.setCreatedAt("1000");

        postModel savedPost = new postModel();
        savedPost.setId(1L);
        savedPost.setUsername("usman");
        savedPost.setContent("New post");
        savedPost.setCreatedAt("1000");

        when(repository.save(post)).thenReturn(savedPost);

        // Act
        postModel result = repository.save(post);

        // Assert
        assertNotNull(result.getId());
        assertEquals("usman", result.getUsername());
        assertEquals("New post", result.getContent());
        verify(repository, times(1)).save(post);
    }

    @Test
    void testDeletePost() {
        // Arrange
        postModel post = new postModel();
        post.setId(1L);
        post.setUsername("usman");
        post.setContent("Post to delete");
        post.setCreatedAt("1000");

        // Act
        repository.delete(post);

        // Assert
        verify(repository, times(1)).delete(post);
    }

    @Test
    void testFindByUsernameInOrderByCreatedAtDesc() {
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

        Page<postModel> page = new PageImpl<>(List.of(post1, post2));
        
        when(repository.findByUsernameInOrderByCreatedAtDesc(
                List.of("user1", "user2"),
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        )).thenReturn(page);

        // Act
        Page<postModel> result = repository.findByUsernameInOrderByCreatedAtDesc(
                List.of("user1", "user2"),
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        );

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Post 1", result.getContent().get(0).getContent());
        verify(repository).findByUsernameInOrderByCreatedAtDesc(
                List.of("user1", "user2"),
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        );
    }
}
