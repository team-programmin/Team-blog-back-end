package com.blog.eu.dto;

import com.blog.eu.enuns.TiposPeditos;


import jakarta.validation.constraints.NotNull;

public record DtoPedito(
    
    @NotNull TiposPeditos tipo
) {
    
}
