package com.project.matchapi.model;


import jakarta.persistence.*;

@Entity
public class MatchOdd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String specifier;

    private Double odd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    public MatchOdd() {
    }

    public MatchOdd(String specifier, Double odd, Match match) {
        this.specifier = specifier;
        this.odd = odd;
        this.match = match;
    }

    public Long getId() {
        return id;
    }

    public String getSpecifier() {
        return specifier;
    }

    public void setSpecifier(String specifier) {
        this.specifier = specifier;
    }

    public Double getOdd() {
        return odd;
    }

    public void setOdd(Double odd) {
        this.odd = odd;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
