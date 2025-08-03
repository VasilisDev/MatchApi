package com.project.matchapi.fixture;

import com.project.matchapi.model.Match;
import com.project.matchapi.model.MatchOdd;
import com.project.matchapi.model.Sport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchFixture {

    public static Match baseMatch() {
        Match match = new Match();
        match.setDescription("Match description");
        match.setMatchDate(LocalDate.of(2024, 8, 1));
        match.setMatchTime(LocalTime.of(19, 0));
        match.setTeamA("Team Alpha");
        match.setTeamB("Team Beta");
        match.setSport(Sport.FOOTBALL);

        MatchOdd odd = new MatchOdd();
        odd.setSpecifier("1");
        odd.setOdd(1.70);
        odd.setMatch(match);

        List<MatchOdd> odds = new ArrayList<>();
        odds.add(odd);
        match.setMatchOdds(odds);

        return match;
    }

    public static Match copy(Match orig) {
        Match match = new Match();
        match.setDescription(orig.getDescription());
        match.setMatchDate(orig.getMatchDate());
        match.setMatchTime(orig.getMatchTime());
        match.setTeamA(orig.getTeamA());
        match.setTeamB(orig.getTeamB());
        match.setSport(orig.getSport());

        if (orig.getMatchOdds() != null) {
            List<MatchOdd> newOdds = new ArrayList<>();
            for (MatchOdd o : orig.getMatchOdds()) {
                MatchOdd c = new MatchOdd();
                c.setSpecifier(o.getSpecifier());
                c.setOdd(o.getOdd());
                c.setMatch(match);
                newOdds.add(c);
            }
            match.setMatchOdds(newOdds);
        } else {
            match.setMatchOdds(Collections.emptyList());
        }
        return match;
    }

    public static Match withOdds(String... specifiers) {
        Match match = baseMatch();
        List<MatchOdd> odds = new ArrayList<>();
        for (String s : specifiers) {
            MatchOdd o = new MatchOdd();
            o.setSpecifier(s);
            o.setOdd(2.0);
            o.setMatch(match);
            odds.add(o);
        }
        match.setMatchOdds(odds);
        return match;
    }
}
