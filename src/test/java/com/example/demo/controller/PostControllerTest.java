package com.example.demo.controller;

import com.example.demo.service.postService;
import com.example.demo.dto.postResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Test
    void testGetPostsByUsername() {
        // Arrange
        postResponse post = new postResponse(1L, "Hello", "usman", "1000");
        
        postService service = mock(postService.class);
        when(service.getPostsByUsername("usman", 0, 10))
                .thenReturn(List.of(post));

        postController controller = new postController();
        ReflectionTestUtils.setField(controller, "service", service);

        // Act
        var response = controller.getPostsByUsername("usman", 0, 10);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Hello", response.getBody().get(0).content());
        assertEquals("usman", response.getBody().get(0).username());
        verify(service).getPostsByUsername("usman", 0, 10);
    }

    @Test
    void testGetPostsByUsernameWithMultiplePosts() {
        // Arrange
        postResponse post1 = new postResponse(1L, "First post", "usman", "1000");
        postResponse post2 = new postResponse(2L, "Second post", "usman", "2000");
        postResponse post3 = new postResponse(3L, "Third post", "usman", "3000");
        
        postService service = mock(postService.class);
        when(service.getPostsByUsername("usman", 0, 10))
                .thenReturn(List.of(post1, post2, post3));

        postController controller = new postController();
        ReflectionTestUtils.setField(controller, "service", service);

        // Act
        var response = controller.getPostsByUsername("usman", 0, 10);

        // Assert
        assertEquals(3, response.getBody().size());
        assertEquals("First post", response.getBody().get(0).content());
        assertEquals("Second post", response.getBody().get(1).content());
        assertEquals("Third post", response.getBody().get(2).content());
        verify(service).getPostsByUsername("usman", 0, 10);
    }

    @Test
    void testGetPostsByUsernameWithEmptyResult() {
        // Arrange
        postService service = mock(postService.class);
        when(service.getPostsByUsername("nonexistent", 0, 10))
                .thenReturn(List.of());

        postController controller = new postController();
        ReflectionTestUtils.setField(controller, "service", service);

        // Act
        var response = controller.getPostsByUsername("nonexistent", 0, 10);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(service).getPostsByUsername("nonexistent", 0, 10);
    }

    @Test
    void testGetPostsByUsernameWithDifferentPagination() {
        // Arrange
        postResponse post1 = new postResponse(11L, "Post 11", "usman", "11000");
        postResponse post2 = new postResponse(12L, "Post 12", "usman", "12000");
        
        postService service = mock(postService.class);
        when(service.getPostsByUsername("usman", 1, 10))
                .thenReturn(List.of(post1, post2));

        postController controller = new postController();
        ReflectionTestUtils.setField(controller, "service", service);

        // Act
        var response = controller.getPostsByUsername("usman", 1, 10);

        // Assert
        assertEquals(2, response.getBody().size());
        assertEquals(11L, response.getBody().get(0).id());
        assertEquals(12L, response.getBody().get(1).id());
        verify(service).getPostsByUsername("usman", 1, 10);
    }

    @Test
    void testGetPostsByUsernameVerifyServiceCall() {
        // Arrange
        postService service = mock(postService.class);
        when(service.getPostsByUsername("testuser", 2, 20))
                .thenReturn(List.of());

        postController controller = new postController();
        ReflectionTestUtils.setField(controller, "service", service);

        // Act
        controller.getPostsByUsername("testuser", 2, 20);

        // Assert - verify exact call parameters
        verify(service, times(1)).getPostsByUsername("testuser", 2, 20);
        verify(service, never()).getPostsByUsername("testuser", 0, 10);
    }
}
