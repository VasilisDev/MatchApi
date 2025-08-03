package com.project.matchapi.dto;

import com.project.matchapi.model.Sport;
import com.project.matchapi.validation.UniqueOddSpecifiers;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MatchRequest(
        @Schema(
                description = "Match description",
                example = "Osfp vs Paok"
        )
        @NotBlank String description,

        @Schema(
                description = "Match date (YYYY-MM-DD)",
                example = "2025-08-04"
        )
        @NotNull LocalDate matchDate,

        @Schema(
                description = "Match start time (HH:mm:ss)",
                example = "19:00:00"
        )
        @NotNull LocalTime matchTime,

        @Schema(
                description = "Team A name",
                example = "Olympiacos"
        )
        @NotBlank String teamA,

        @Schema(
                description = "Team B name",
                example = "Paok"
        )
        @NotBlank String teamB,


        @Schema(
                description = "Sport type, can be FOOTBALL or BASKETBALL",
                example = "FOOTBALL"
        )
        @NotNull Sport sport,

        @ArraySchema(
                schema = @Schema(implementation = MatchOddRequest.class),
                arraySchema = @Schema(description = "List of odds for this match")
        )
        @NotNull
        @Valid
        @UniqueOddSpecifiers
        List<MatchOddRequest> matchOdds
) {
}
