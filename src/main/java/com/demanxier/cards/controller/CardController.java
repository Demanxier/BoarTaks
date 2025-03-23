package com.demanxier.cards.controller;

import com.demanxier.cards.dto.CardAtualizarDTO;
import com.demanxier.cards.dto.CardRequestDTO;
import com.demanxier.cards.dto.CardResponseColunaDTO;
import com.demanxier.cards.dto.CardResponseDTO;
import com.demanxier.cards.model.Card;
import com.demanxier.cards.model.StatusColuna;
import com.demanxier.cards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@CrossOrigin(origins = "*")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponseDTO> criarCard(@Valid @RequestBody CardRequestDTO cardDTO){
        Card card = cardService.criarCard(cardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CardResponseDTO(card));
    }

    @PutMapping("/{id}/move")
    public ResponseEntity<CardResponseDTO> moverCard(@PathVariable Long id, @RequestParam StatusColuna newStatus){
        Card card = cardService.moverCard(id, newStatus);
        return ResponseEntity.ok(new CardResponseDTO(card));
    }

    @GetMapping("/relatorio/concluido")
    public ResponseEntity<List<CardResponseDTO>> getCompletedReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        List<Card> cards = cardService.buscarConcluidos(inicio, fim);
        return ResponseEntity.ok(cards.stream().map(CardResponseDTO::new).toList());
    }

    @GetMapping
    public ResponseEntity<List<CardResponseColunaDTO>> getCardsStatus(@RequestParam(required = false) StatusColuna status){
            List<Card> cards = cardService.buscarCardsStatus(status);
            return ResponseEntity.ok(cards.stream().map(CardResponseColunaDTO::new).toList());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<CardResponseDTO> atuaizarCard(@PathVariable Long id, @RequestBody CardAtualizarDTO atualizarDTO){
        return cardService.atualizarCard(id, atualizarDTO.descricao())
                .map(card -> ResponseEntity.ok(new CardResponseDTO(card)))
                .orElse(ResponseEntity.notFound().build());
    }
}
