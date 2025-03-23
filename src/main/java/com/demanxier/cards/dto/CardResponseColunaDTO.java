package com.demanxier.cards.dto;

import com.demanxier.cards.model.Card;
import com.demanxier.cards.model.StatusColuna;

import java.time.LocalDateTime;

public record CardResponseColunaDTO(
        Long id,
        String titulo,
        String descricao,
        StatusColuna statusColuna,
        LocalDateTime dataCriacao,
        LocalDateTime dataConclusao
) {
    public CardResponseColunaDTO(Card card){
        this(
                card.getId(),
                card.getTitulo(),
                card.getDescricao(),
                card.getStatusColuna(),
                card.getDataCriacao(),
                card.getDataConclusao()
        );
    }
}
