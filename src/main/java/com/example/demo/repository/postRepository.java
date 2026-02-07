package com.example.demo.repository;
import com.example.demo.model.postModel;

import java.util.List;

// import org.hibernate.query.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface postRepository extends JpaRepository<postModel, Long> {    
    Page<postModel> findByUsernameInOrderByCreatedAtDesc(List<String> usernames, Pageable pageable);

    @Query("""
    SELECT p
    FROM postModel p
    WHERE p.username = :username
    ORDER BY p.createdAt DESC
    """)
    Page<postModel> findPostsByUsername(
    @Param("username") String username,
    Pageable pageable
);

    
}
