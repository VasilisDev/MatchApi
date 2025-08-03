package com.project.matchapi.service;

import com.project.matchapi.fixture.MatchFixture;
import com.project.matchapi.model.Match;
import com.project.matchapi.model.MatchOdd;
import com.project.matchapi.repository.MatchRepository;
import com.project.matchapi.service.exception.MatchNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchService matchService;

    private Match testMatch;

    @BeforeEach
    void setUp() {
        testMatch = MatchFixture.baseMatch();
    }

    @Test
    void givenExistingMatch_whenGetById_thenReturnMatch() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));

        Match result = matchService.getMatchById(1L);

        assertThat(result).isSameAs(testMatch);
        verify(matchRepository).findById(1L);
    }

    @Test
    void givenNonexistentMatch_whenGetById_thenThrowException() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.getMatchById(99L))
                .isInstanceOf(MatchNotFoundException.class)
                .hasMessageContaining("Match not found with id=99");

        verify(matchRepository).findById(99L);
    }

    @Test
    void givenValidNewMatch_whenCreateMatch_thenSavedWithOdds() {
        Match toCreate = MatchFixture.copy(testMatch);
        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        Match created = matchService.createMatch(toCreate);

        assertThat(created.getMatchOdds()).hasSize(1);
        MatchOdd odd = created.getMatchOdds().get(0);
        assertThat(odd.getMatch()).isSameAs(created);
        verify(matchRepository).save(toCreate);
    }

    @Test
    void givenUpdateWithNullOdds_whenUpdateMatch_thenKeepsExistingOdds() {
        Match existing = MatchFixture.copy(testMatch);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(existing));
        Match updated = new Match();
        updated.setDescription("Updated Description");
        updated.setMatchDate(existing.getMatchDate());
        updated.setMatchTime(existing.getMatchTime());
        updated.setTeamA(existing.getTeamA());
        updated.setTeamB(existing.getTeamB());
        updated.setSport(existing.getSport());
        updated.setMatchOdds(null);

        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        Match result = matchService.updateMatch(1L, updated);

        assertThat(result.getMatchOdds()).hasSize(1);
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        verify(matchRepository).save(existing);
    }

    @Test
    void givenUpdateWithNewOdds_whenUpdateMatch_thenReplacesOdds() {
        Match existing = MatchFixture.copy(testMatch);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(existing));
        Match updated = new Match();
        updated.setDescription("Updated Description");
        updated.setMatchDate(existing.getMatchDate());
        updated.setMatchTime(existing.getMatchTime());
        updated.setTeamA(existing.getTeamA());
        updated.setTeamB(existing.getTeamB());
        updated.setSport(existing.getSport());

        MatchOdd newOdd = new MatchOdd();
        newOdd.setSpecifier("X");
        newOdd.setOdd(3.3);
        newOdd.setMatch(updated);
        updated.setMatchOdds(Collections.singletonList(newOdd));

        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        Match result = matchService.updateMatch(1L, updated);

        assertThat(result.getMatchOdds()).hasSize(1);
        assertThat(result.getMatchOdds().get(0).getSpecifier()).isEqualTo("X");
        verify(matchRepository).save(existing);
    }

    @Test
    void givenMissingMatch_whenUpdate_thenThrowNotFound() {
        when(matchRepository.findById(5L)).thenReturn(Optional.empty());
        Match updated = new Match();
        updated.setDescription("Whatever");

        assertThatThrownBy(() -> matchService.updateMatch(5L, updated))
                .isInstanceOf(MatchNotFoundException.class);

        verify(matchRepository).findById(5L);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void givenExistingMatch_whenDelete_thenDeletes() {
        when(matchRepository.existsById(2L)).thenReturn(true);

        matchService.deleteMatch(2L);

        verify(matchRepository).deleteById(2L);
    }

    @Test
    void givenMissingMatch_whenDelete_thenThrowNotFound() {
        when(matchRepository.existsById(3L)).thenReturn(false);

        assertThatThrownBy(() -> matchService.deleteMatch(3L))
                .isInstanceOf(MatchNotFoundException.class);
        verify(matchRepository).existsById(3L);
        verify(matchRepository, never()).deleteById(any());
    }
}
