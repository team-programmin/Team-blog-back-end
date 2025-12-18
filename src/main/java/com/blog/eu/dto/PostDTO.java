package com.blog.eu.dto;

import java.util.List;

public record PostDTO(
    Long id,
    String title,
    String content,
    AuthorDTO author,
    String createdAt,
    List<ComentarioDTO> comments
) {}
