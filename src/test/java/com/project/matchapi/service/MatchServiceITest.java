package com.project.matchapi.service;

import com.project.matchapi.fixture.MatchFixture;
import com.project.matchapi.model.Match;
import com.project.matchapi.model.Sport;
import com.project.matchapi.repository.MatchRepository;
import com.project.matchapi.service.exception.MatchNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest
class MatchServiceITest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("matchdb-test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    MatchService matchService;

    @Autowired
    MatchRepository matchRepository;

    @BeforeEach
    void clean() {
        matchRepository.deleteAll();
    }

    @Test
    void whenGetAllMatches_thenReturnsAllPersisted() {
        matchService.createMatch(MatchFixture.baseMatch());
        matchService.createMatch(MatchFixture.baseMatch());

        assertThat(matchService.getAllMatches()).hasSize(2);
    }

    @Test
    void givenValidMatch_whenCreateAndRetrieve_thenPersistedCorrectly() {
        Match m = MatchFixture.baseMatch();
        m.setDescription("Aek-Paok");
        m.setMatchDate(LocalDate.of(2025, 9, 1));
        m.setMatchTime(LocalTime.of(18, 30));
        m.setTeamA("Aek");
        m.setTeamB("Paok");
        m.setSport(Sport.BASKETBALL);
        m.getMatchOdds().get(0).setSpecifier("X");

        Match created = matchService.createMatch(m);
        Match fetched = matchService.getMatchById(created.getId());

        assertThat(fetched).isNotNull();
        assertThat(fetched.getMatchOdds()).hasSize(1);
        assertThat(fetched.getMatchOdds().get(0).getSpecifier()).isEqualTo("X");
    }

    @Test
    void givenExistingMatch_whenUpdateDescriptionAndClearOdds_thenPersisted() {
        Match m = matchService.createMatch(MatchFixture.baseMatch());
        m.setDescription("Updated Description");
        m.setMatchOdds(List.of());

        matchService.updateMatch(m.getId(), m);
        Match fetched = matchService.getMatchById(m.getId());

        assertThat(fetched.getDescription()).isEqualTo("Updated Description");
        assertThat(fetched.getMatchOdds()).isEmpty();
    }

    @Test
    void givenExistingMatch_whenDelete_thenCannotFetch() {
        Match m = matchService.createMatch(MatchFixture.baseMatch());
        matchService.deleteMatch(m.getId());
        assertThatThrownBy(() -> matchService.getMatchById(m.getId()))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenMissingMatch_whenDelete_thenThrowNotFound() {
        assertThatThrownBy(() -> matchService.deleteMatch(999L))
                .isInstanceOf(MatchNotFoundException.class);
    }
}
