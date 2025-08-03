package com.project.matchapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.matchapi.dto.MatchOddRequest;
import com.project.matchapi.dto.MatchRequest;
import com.project.matchapi.fixture.MatchFixture;
import com.project.matchapi.fixture.RequestFixture;
import com.project.matchapi.model.Match;
import com.project.matchapi.model.Sport;
import com.project.matchapi.service.MatchService;
import com.project.matchapi.service.exception.MatchNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
class MatchControllerITest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MatchService matchService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void givenMultipleMatches_whenGetAll_thenReturnsAllInResponse() throws Exception {
        Match match1 = MatchFixture.baseMatch();
        Match match2 = MatchFixture.copy(match1);
        match2.setTeamA("OSFP");
        match2.setTeamB("PAO");
        when(matchService.getAllMatches()).thenReturn(List.of(match1, match2));

        mockMvc.perform(get("/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].teamA").value("Team Alpha"))
                .andExpect(jsonPath("$[1].teamA").value("OSFP"))
                .andExpect(jsonPath("$[1].teamB").value("PAO"));
    }

    @Test
    void givenMatch_whenGetOne_thenReturnsThatMatch() throws Exception {
        Match match = MatchFixture.baseMatch();
        when(matchService.getMatchById(1L)).thenReturn(match);

        mockMvc.perform(get("/matches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamA").value("Team Alpha"))
                .andExpect(jsonPath("$.teamB").value("Team Beta"))
                .andExpect(jsonPath("$.description").value(match.getDescription()));
    }

    @Test
    void givenMissingMatch_whenGetOne_thenReturns404WithMessage() throws Exception {
        when(matchService.getMatchById(123L)).thenThrow(new MatchNotFoundException(123L));

        mockMvc.perform(get("/matches/123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Match not found")));
    }

    @Test
    void givenValidRequest_whenCreate_thenCreatesAndReturnsMatch() throws Exception {
        MatchRequest req = RequestFixture.matchRequest();
        Match created = MatchFixture.baseMatch();
        when(matchService.createMatch(any())).thenReturn(created);

        mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamA").value("Team Alpha"))
                .andExpect(jsonPath("$.teamB").value("Team Beta"))
                .andExpect(jsonPath("$.matchOdds.length()").value(1))
                .andExpect(jsonPath("$.matchOdds[0].specifier").value("1"));
    }

    @Test
    void givenValidRequest_whenUpdate_thenUpdatesAndReturnsMatch() throws Exception {
        MatchRequest req = RequestFixture.matchRequest();
        Match updated = MatchFixture.baseMatch();
        when(matchService.updateMatch(eq(42L), any())).thenReturn(updated);

        mockMvc.perform(put("/matches/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamA").value("Team Alpha"))
                .andExpect(jsonPath("$.teamB").value("Team Beta"))
                .andExpect(jsonPath("$.matchOdds.length()").value(1));
    }

    @Test
    void givenMissingMatch_whenUpdate_thenReturns404WithMessage() throws Exception {
        MatchRequest req = RequestFixture.matchRequest();
        when(matchService.updateMatch(eq(2L), any())).thenThrow(new MatchNotFoundException(2L));

        mockMvc.perform(put("/matches/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Match not found with id=2")));
    }

    @Test
    void givenExistingMatch_whenDelete_thenDeletes() throws Exception {
        doNothing().when(matchService).deleteMatch(100L);

        mockMvc.perform(delete("/matches/100"))
                .andExpect(status().isOk());
        verify(matchService).deleteMatch(100L);
    }

    @Test
    void givenMissingMatch_whenDelete_thenReturns404WithMessage() throws Exception {
        doThrow(new MatchNotFoundException(1000L)).when(matchService).deleteMatch(1000L);

        mockMvc.perform(delete("/matches/1000"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(is("Match not found with id=1000")));
    }

    @Test
    void givenMissingRequiredField_whenPostMatch_thenValidationFails() throws Exception {
        MatchRequest invalidReq = new MatchRequest(
                "desc",
                LocalDate.now(),
                LocalTime.NOON,
                null,
                "Team Beta",
                Sport.FOOTBALL,
                List.of(new MatchOddRequest("1", 1.5))
        );

        mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("teamA")));
    }

    @Test
    void givenDuplicateOddSpecifiers_whenPostMatch_thenValidationFails() throws Exception {
        MatchRequest req = RequestFixture.matchRequest();
        MatchRequest invalidReq = new MatchRequest(
                req.description(),
                req.matchDate(),
                req.matchTime(),
                req.teamA(),
                req.teamB(),
                req.sport(),
                List.of(
                        new MatchOddRequest("1", 1.77),
                        new MatchOddRequest("1", 2.11)
                )
        );

        mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Duplicate odd specifier")));
    }

    @Test
    void givenServiceThrowsGenericException_whenMatchesEndpoint_thenReturns500() throws Exception {
        when(matchService.getAllMatches()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/matches"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("An unexpected error occurred")));
    }
}
