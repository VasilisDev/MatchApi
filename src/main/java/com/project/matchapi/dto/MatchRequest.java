package com.project.matchapi.dto;

import com.project.matchapi.model.Sport;
import com.project.matchapi.validation.UniqueOddSpecifiers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MatchRequest(
        @NotBlank String description,
        @NotNull LocalDate matchDate,
        @NotNull LocalTime matchTime,
        @NotBlank String teamA,
        @NotBlank String teamB,
        @NotNull Sport sport,
        @NotNull
        @Valid
        @UniqueOddSpecifiers
        List<MatchOddRequest> matchOdds
) {
}
