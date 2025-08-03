package com.project.matchapi.repository;

import com.project.matchapi.model.Match;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @EntityGraph(attributePaths = "matchOdds")
    @Override
    List<Match> findAll();

    @EntityGraph(attributePaths = "matchOdds")
    @Override
    Optional<Match> findById(Long id);
}
