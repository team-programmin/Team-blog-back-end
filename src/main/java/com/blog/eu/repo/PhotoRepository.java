package com.blog.eu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.eu.model.Photo;
import java.util.Optional;
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Optional<Photo> findByKey(String key);
}