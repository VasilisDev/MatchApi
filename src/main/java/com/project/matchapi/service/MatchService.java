package com.project.matchapi.service;

import com.project.matchapi.model.Match;
import com.project.matchapi.model.MatchOdd;
import com.project.matchapi.repository.MatchRepository;
import com.project.matchapi.service.exception.MatchNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new MatchNotFoundException(id));
    }

    @Transactional
    public Match createMatch(Match match) {
        if (match.getMatchOdds() != null) {
            match.getMatchOdds().forEach(odd -> odd.setMatch(match));
        }
        return matchRepository.save(match);
    }

    @Transactional
    public Match updateMatch(Long id, Match updatedMatch) {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new MatchNotFoundException(id));

        existingMatch.setDescription(updatedMatch.getDescription());
        existingMatch.setMatchDate(updatedMatch.getMatchDate());
        existingMatch.setMatchTime(updatedMatch.getMatchTime());
        existingMatch.setTeamA(updatedMatch.getTeamA());
        existingMatch.setTeamB(updatedMatch.getTeamB());
        existingMatch.setSport(updatedMatch.getSport());

        if (updatedMatch.getMatchOdds() != null) {
            existingMatch.getMatchOdds().clear();
            matchRepository.flush();

            for (MatchOdd odd : updatedMatch.getMatchOdds()) {
                odd.setMatch(existingMatch);
                existingMatch.getMatchOdds().add(odd);
            }
        }

        return existingMatch;
    }

    @Transactional
    public void deleteMatch(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new MatchNotFoundException(id);
        }
        matchRepository.deleteById(id);
    }
}
