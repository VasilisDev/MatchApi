package com.project.matchapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MatchOddRequest(
        @NotBlank String specifier,
        @NotNull @Positive Double odd
) {
}
