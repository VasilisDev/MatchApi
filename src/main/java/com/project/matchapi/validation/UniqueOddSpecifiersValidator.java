package com.project.matchapi.validation;

import com.project.matchapi.dto.MatchOddRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Objects;

public class UniqueOddSpecifiersValidator implements ConstraintValidator<UniqueOddSpecifiers, List<MatchOddRequest>> {

    @Override
    public boolean isValid(List<MatchOddRequest> odds, ConstraintValidatorContext context) {
        if (odds == null) {
            return true;
        }

        long distinct = odds.stream()
                .map(MatchOddRequest::specifier)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        boolean valid = distinct == odds.size();
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
        }
        return valid;
    }
}
