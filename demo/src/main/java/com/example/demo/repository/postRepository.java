package com.example.demo.repository;
import com.example.demo.model.postModel;

import java.util.List;

// import org.hibernate.query.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface postRepository extends JpaRepository<postModel, Long> {    
    Page<postModel> findByUsernameInOrderByCreatedAtDesc(List<String> usernames, Pageable pageable);
    
}
