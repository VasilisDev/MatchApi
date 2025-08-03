package com.project.matchapi.controller;

import com.project.matchapi.dto.MatchOddResponse;
import com.project.matchapi.dto.MatchRequest;
import com.project.matchapi.dto.MatchResponse;
import com.project.matchapi.model.Match;
import com.project.matchapi.model.MatchOdd;
import com.project.matchapi.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public List<MatchResponse> getAll() {
        return matchService.getAllMatches().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MatchResponse getOne(@PathVariable Long id) {
        Match match = matchService.getMatchById(id);
        return toResponse(match);
    }

    @PostMapping
    public MatchResponse create(@Valid @RequestBody MatchRequest req) {
        Match built = fromRequest(req);
        Match saved = matchService.createMatch(built);
        return toResponse(saved);
    }

    @PutMapping("/{id}")
    public MatchResponse update(@PathVariable Long id, @Valid @RequestBody MatchRequest req) {
        Match built = fromRequest(req);
        Match updated = matchService.updateMatch(id, built);
        return toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        matchService.deleteMatch(id);
    }

    private MatchResponse toResponse(Match m) {
        return new MatchResponse(
                m.getId(),
                m.getDescription(),
                m.getMatchDate(),
                m.getMatchTime(),
                m.getTeamA(),
                m.getTeamB(),
                m.getSport(),
                m.getMatchOdds() == null ? emptyList() :
                        m.getMatchOdds().stream()
                                .map(o -> new MatchOddResponse(o.getId(), o.getSpecifier(), o.getOdd()))
                                .collect(Collectors.toList())
        );
    }

    private Match fromRequest(MatchRequest req) {
        Match m = new Match();
        m.setDescription(req.description());
        m.setMatchDate(req.matchDate());
        m.setMatchTime(req.matchTime());
        m.setTeamA(req.teamA());
        m.setTeamB(req.teamB());
        m.setSport(req.sport());
        if (req.matchOdds() != null) {
            var odds = req.matchOdds().stream().map(o -> {
                MatchOdd mo = new MatchOdd();
                mo.setSpecifier(o.specifier());
                mo.setOdd(o.odd());
                mo.setMatch(m);
                return mo;
            }).collect(Collectors.toList());
            m.setMatchOdds(odds);
        }
        return m;
    }
}


// Mapping helpers


