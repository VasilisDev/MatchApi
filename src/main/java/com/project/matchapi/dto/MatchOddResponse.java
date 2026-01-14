package com.project.matchapi.dto;

public record MatchOddResponse(
        Long id,
        String specifier,
        Double odd
) {
}
