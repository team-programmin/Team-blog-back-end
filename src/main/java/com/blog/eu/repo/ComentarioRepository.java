package com.blog.eu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.eu.model.Comentario;
import com.blog.eu.model.Post;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPost(com.blog.eu.model.Post post);
    List<Comentario> findByPostAndParentIsNull(Post post);

}
