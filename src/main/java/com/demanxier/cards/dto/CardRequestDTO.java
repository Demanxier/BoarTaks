package com.demanxier.cards.dto;

import jakarta.validation.constraints.NotBlank;

public record CardRequestDTO (
        @NotBlank String titulo,
        String descricao
){}
