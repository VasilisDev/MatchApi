package com.project.matchapi.validation;

import com.project.matchapi.dto.MatchOddRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UniqueOddSpecifiersValidatorTest {

    private UniqueOddSpecifiersValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new UniqueOddSpecifiersValidator();
        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);
    }

    @Test
    void givenNullOdds_whenIsValid_thenTrue() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    void givenEmptyOdds_whenIsValid_thenTrue() {
        assertThat(validator.isValid(List.of(), context)).isTrue();
    }

    @Test
    void givenAllUniqueSpecifiers_whenIsValid_thenTrue() {
        var odds = List.of(
                new MatchOddRequest("1", 1.5),
                new MatchOddRequest("X", 2.2),
                new MatchOddRequest("2", 3.1)
        );

        assertThat(validator.isValid(odds, context)).isTrue();
    }

    @Test
    void givenDuplicateSpecifiers_whenIsValid_thenFalse() {
        var odds = List.of(
                new MatchOddRequest("1", 1.5),
                new MatchOddRequest("1", 2.1)
        );

        assertThat(validator.isValid(odds, context)).isFalse();

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void givenSomeNullSpecifiers_whenIsValid_thenTrueIfNoDuplicates() {
        var odds = List.of(
                new MatchOddRequest(null, 1.5),
                new MatchOddRequest("1", 2.2),
                new MatchOddRequest(null, 2.5)
        );

        assertThat(validator.isValid(odds, context)).isFalse();
    }

    @Test
    void givenMultipleNullSpecifiersAndDuplicates_whenIsValid_thenFalse() {
        var odds = List.of(
                new MatchOddRequest(null, 1.5),
                new MatchOddRequest("1", 2.2),
                new MatchOddRequest("1", 2.5),
                new MatchOddRequest(null, 2.8)
        );

        assertThat(validator.isValid(odds, context)).isFalse();

        verify(context, atLeastOnce()).disableDefaultConstraintViolation();
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(any());
    }
}
