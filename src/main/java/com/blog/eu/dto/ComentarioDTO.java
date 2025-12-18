package com.blog.eu.dto;

import java.util.List;

public record ComentarioDTO(
    Long id,
    String texto,
    AuthorDTO autor,
    String dataCriacao,
    Boolean modificado,
    List<ComentarioDTO> respostas
) {}
