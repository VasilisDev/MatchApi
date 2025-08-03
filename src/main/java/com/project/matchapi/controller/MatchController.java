package com.project.matchapi.controller;

import com.project.matchapi.dto.MatchOddResponse;
import com.project.matchapi.dto.MatchRequest;
import com.project.matchapi.dto.MatchResponse;
import com.project.matchapi.model.Match;
import com.project.matchapi.model.MatchOdd;
import com.project.matchapi.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Operation(
            summary = "Get all matches",
            description = "Returns a list of all matches with their odds.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of matches",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchResponse.class)))
            }
    )
    @GetMapping
    public List<MatchResponse> getAll() {
        return matchService.getAllMatches().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Get a single match by id",
            description = "Returns the match details for the given id, including odds.",
            parameters = @Parameter(name = "id", description = "Match id", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Match found",
                            content = @Content(schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Match not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Validation failed or unsupported enum value.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public MatchResponse getOne(@PathVariable Long id) {
        Match match = matchService.getMatchById(id);
        return toResponse(match);
    }

    @Operation(
            summary = "Create a new match",
            description = "Creates a match with the specified details and odds.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Match creation payload including odds",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MatchRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Match created",
                            content = @Content(schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Validation failed or unsupported enum value.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )

            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchResponse create(@Valid @RequestBody MatchRequest req) {
        Match built = fromRequest(req);
        Match saved = matchService.createMatch(built);
        return toResponse(saved);
    }


    @Operation(
            summary = "Update an existing match",
            description = "Updates a match (and odds) and returns the updated entity.",
            parameters = @Parameter(name = "id", description = "ID of the match to update", required = true, example = "1"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Match update payload including odds",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MatchRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Match updated",
                            content = @Content(schema = @Schema(implementation = MatchResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Match not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))

                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Validation failed or unsupported enum value.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PutMapping("/{id}")
    public MatchResponse update(@PathVariable Long id, @Valid @RequestBody MatchRequest req) {
        Match built = fromRequest(req);
        Match updated = matchService.updateMatch(id, built);
        return toResponse(updated);
    }

    @Operation(
            summary = "Delete a match",
            description = "Deletes a match by id",
            parameters = @Parameter(name = "id", description = "ID of the match to delete", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Match deleted"),
                    @ApiResponse(responseCode = "404", description = "Match not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        matchService.deleteMatch(id);
    }

    private MatchResponse toResponse(Match m) {
        return new MatchResponse(
                m.getId(),
                m.getDescription(),
                m.getMatchDate(),
                m.getMatchTime(),
                m.getTeamA(),
                m.getTeamB(),
                m.getSport(),
                m.getMatchOdds() == null ? emptyList() :
                        m.getMatchOdds().stream()
                                .map(o -> new MatchOddResponse(o.getId(), o.getSpecifier(), o.getOdd()))
                                .collect(Collectors.toList())
        );
    }

    private Match fromRequest(MatchRequest req) {
        Match m = new Match();
        m.setDescription(req.description());
        m.setMatchDate(req.matchDate());
        m.setMatchTime(req.matchTime());
        m.setTeamA(req.teamA());
        m.setTeamB(req.teamB());
        m.setSport(req.sport());
        if (req.matchOdds() != null) {
            var odds = req.matchOdds().stream().map(o -> {
                MatchOdd mo = new MatchOdd();
                mo.setSpecifier(o.specifier());
                mo.setOdd(o.odd());
                mo.setMatch(m);
                return mo;
            }).collect(Collectors.toList());
            m.setMatchOdds(odds);
        }
        return m;
    }
}
