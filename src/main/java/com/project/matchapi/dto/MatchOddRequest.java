package com.project.matchapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MatchOddRequest(
            @NotBlank String specifier,
            @NotNull Double odd
        ) {}
