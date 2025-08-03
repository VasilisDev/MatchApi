package com.project.matchapi.dto;

import com.project.matchapi.model.Sport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MatchResponse(
        Long id,
        String description,
        LocalDate matchDate,
        LocalTime matchTime,
        String teamA,
        String teamB,
        Sport sport,
        List<MatchOddResponse> matchOdds
        ) {}
