package com.project.matchapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MatchOddRequest(
        @Schema(
                description = "Specifier for the odd, for instance '1', 'X', '2'",
                example = "1"
        )
        @NotBlank String specifier,

        @Schema(
                description = "Odd value, must be a positive number",
                example = "2.15",
                minimum = "0.01"
        )
        @NotNull @Positive Double odd
) {
}
