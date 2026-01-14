package com.project.matchapi.fixture;

import com.project.matchapi.dto.MatchOddRequest;
import com.project.matchapi.dto.MatchRequest;
import com.project.matchapi.model.Sport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RequestFixture {
    public static MatchRequest matchRequest() {
        return new MatchRequest(
                "test description",
                LocalDate.of(2025, 1, 8),
                LocalTime.of(15, 0),
                "Team Alpha",
                "Team Beta",
                Sport.FOOTBALL,
                List.of(new MatchOddRequest("1", 1.77))
        );
    }
}

