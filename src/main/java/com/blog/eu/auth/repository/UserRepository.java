package com.blog.eu.auth.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<com.blog.eu.model.User, Long> {
    Optional<com.blog.eu.model.User> findByEmail(String email);
   
}