package com.blog.eu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.eu.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  
}

