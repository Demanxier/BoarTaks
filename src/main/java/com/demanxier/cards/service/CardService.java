package com.demanxier.cards.service;

import com.demanxier.cards.dto.CardRequestDTO;
import com.demanxier.cards.model.Card;
import com.demanxier.cards.model.StatusColuna;
import com.demanxier.cards.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository){
        this.cardRepository = cardRepository;
    }

    public Card criarCard(CardRequestDTO cardDTO){
        Card novoCard = new Card();
        novoCard.setTitulo(cardDTO.titulo());
        novoCard.setDescricao(cardDTO.descricao());
        novoCard.setStatusColuna(StatusColuna.A_FAZER);
        return cardRepository.save(novoCard);
    }

    public Optional<Card> atualizarCard(Long id, String novaDescricao){
        return cardRepository.findById(id)
                .map(card -> {
                    card.setDescricao(novaDescricao);
                    return cardRepository.save(card);
                });
    }

    public Card moverCard(Long id, StatusColuna novoStatus){
        return cardRepository.findById(id)
                .map(card -> {
                    validarTransicao(card.getStatusColuna(), novoStatus);

                    if (novoStatus == StatusColuna.CONCLUIDO){
                        card.setDataConclusao(LocalDateTime.now());
                    } else if (card.getStatusColuna() == StatusColuna.CONCLUIDO) {
                        card.setDataConclusao(null);
                    }

                    card.setStatusColuna(novoStatus);
                    return cardRepository.save(card);
                })
                .orElseThrow(()-> new RuntimeException("Card não encontrado."));
    }

    private void validarTransicao(StatusColuna atual, StatusColuna novo){
        if(novo == StatusColuna.A_FAZER){
            throw new IllegalArgumentException("Não pode voltar o Card para o Status inicial. Pause ele.");
        }

        if(atual == StatusColuna.CONCLUIDO && novo != StatusColuna.CONCLUIDO) {
            throw new IllegalArgumentException("Card concluído não pode ser alterado");
        }

    }

    public List<Card> buscarConcluidos(LocalDateTime inicio, LocalDateTime fim){
        if(inicio != null && fim != null){
            return cardRepository.findCompleteBetweenDatas(inicio, fim);
        }
        return cardRepository.findRecentCompletado(LocalDateTime.now().minusDays(15));
    }

    public List<Card> buscarCardsStatus(StatusColuna status){
        return cardRepository.findByStatusColuna(status);
    }

}
