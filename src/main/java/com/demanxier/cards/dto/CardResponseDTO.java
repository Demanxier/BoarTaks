package com.demanxier.cards.dto;

import com.demanxier.cards.model.Card;
import com.demanxier.cards.model.StatusColuna;

import java.time.LocalDateTime;


public record CardResponseDTO(
        Long id,
        String titulo,
        String descricao,
        StatusColuna statusColuna,
        LocalDateTime dataConclusao
) {

    public CardResponseDTO(Card card){
        this(
                card.getId(),
                card.getTitulo(),
                card.getDescricao(),
                card.getStatusColuna(),
                card.getDataConclusao()
        );
    }
}
