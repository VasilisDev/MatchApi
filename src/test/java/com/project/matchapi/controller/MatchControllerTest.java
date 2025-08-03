package com.project.matchapi.controller;

import com.project.matchapi.dto.MatchRequest;
import com.project.matchapi.dto.MatchResponse;
import com.project.matchapi.fixture.MatchFixture;
import com.project.matchapi.fixture.RequestFixture;
import com.project.matchapi.model.Match;
import com.project.matchapi.service.MatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchController controller;

    @Test
    void givenMultipleMatchesInService_whenGetAll_thenReturnsListOfResponses() {
        var match1 = MatchFixture.baseMatch();
        var match2 = MatchFixture.copy(match1);
        match2.setTeamA("OSFP");
        match2.setTeamB("PAO");

        when(matchService.getAllMatches()).thenReturn(List.of(match1, match2));

        List<MatchResponse> responses = controller.getAll();

        verify(matchService).getAllMatches();
        assertThat(responses).hasSize(2);

        assertThat(responses.get(0).teamA()).isEqualTo("Team Alpha");
        assertThat(responses.get(1).teamA()).isEqualTo("OSFP");
        assertThat(responses.get(1).teamB()).isEqualTo("PAO");
    }


    @Test
    void givenMatchId_whenGetOne_thenReturnsResponse() {
        when(matchService.getMatchById(1L)).thenReturn(MatchFixture.baseMatch());

        MatchResponse response = controller.getOne(1L);

        verify(matchService).getMatchById(1L);
        assertThat(response.teamA()).isEqualTo("Team Alpha");
    }

    @Test
    void givenValidRequest_whenCreate_thenServiceIsCalledAndReturnsTheResponse() {
        MatchRequest request = RequestFixture.matchRequest();
        Match expectedEntity = MatchFixture.baseMatch();
        when(matchService.createMatch(any())).thenReturn(expectedEntity);

        MatchResponse response = controller.create(request);

        ArgumentCaptor<Match> captor = ArgumentCaptor.forClass(Match.class);
        verify(matchService).createMatch(captor.capture());
        Match passedEntity = captor.getValue();

        assertThat(passedEntity.getDescription()).isEqualTo(request.description());
        assertThat(passedEntity.getTeamA()).isEqualTo(request.teamA());
        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo(expectedEntity.getDescription());
        assertThat(response.teamA()).isEqualTo(expectedEntity.getTeamA());
    }

    @Test
    void givenValidUpdateRequest_whenUpdate_thenServiceIsCalledAndReturnsMappedResponse() {
        MatchRequest req = RequestFixture.matchRequest();
        Match expected = MatchFixture.baseMatch();
        when(matchService.updateMatch(eq(42L), any())).thenReturn(expected);

        MatchResponse response = controller.update(42L, req);

        verify(matchService).updateMatch(eq(42L), any());
        assertThat(response.teamA()).isEqualTo("Team Alpha");
    }

    @Test
    void givenMatchId_whenDelete_thenServiceIsCalled() {
        doNothing().when(matchService).deleteMatch(99L);

        controller.delete(99L);

        verify(matchService).deleteMatch(99L);
    }
}
