package com.demanxier.cards.repository;

import com.demanxier.cards.model.Card;
import com.demanxier.cards.model.StatusColuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByStatusColuna(StatusColuna status);

    @Query("SELECT c FROM Card c WHERE c.dataConclusao BETWEEN :inicio AND :fim")
    List<Card> findCompleteBetweenDatas(@Param("inicio")LocalDateTime inicio,
                                        @Param("fim") LocalDateTime fim);

    @Query("SELECT c FROM Card c WHERE c.statusColuna = 'CONCLUIDO' AND c.dataConclusao >= :cutoff")
    List<Card> findRecentCompletado(@Param("cutoff") LocalDateTime cutoff);

}
