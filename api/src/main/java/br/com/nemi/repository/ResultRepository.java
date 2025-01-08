package br.com.nemi.repository;

import br.com.nemi.domain.draw.Draw;
import br.com.nemi.domain.participant.Participant;
import br.com.nemi.domain.result.Result;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByDraw(Draw draw);

    @Transactional
    void deleteAllByDraw(Draw draw);

    Optional<Result> findByAccessCode(String accessCode);

    Optional<Result> findByDrawAndGiver(Draw draw, Participant giver);
}
