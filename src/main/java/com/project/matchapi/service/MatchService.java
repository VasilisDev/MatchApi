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
                .map(m -> {
                    m.setDescription(updatedMatch.getDescription());
                    m.setMatchDate(updatedMatch.getMatchDate());
                    m.setMatchTime(updatedMatch.getMatchTime());
                    m.setTeamA(updatedMatch.getTeamA());
                    m.setTeamB(updatedMatch.getTeamB());
                    m.setSport(updatedMatch.getSport());

                    if (updatedMatch.getMatchOdds() != null) {
                        m.getMatchOdds().clear();
                        for (MatchOdd odd : updatedMatch.getMatchOdds()) {
                            odd.setMatch(m);
                            m.getMatchOdds().add(odd);
                        }
                    }
                    return m;
                })
                .orElseThrow(() -> new MatchNotFoundException(id));

        return matchRepository.save(existingMatch);
    }

    @Transactional
    public void deleteMatch(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new MatchNotFoundException(id);
        }
        matchRepository.deleteById(id);
    }
}
