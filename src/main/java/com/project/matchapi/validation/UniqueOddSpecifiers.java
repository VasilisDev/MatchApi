package com.project.matchapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueOddSpecifiersValidator.class)
public @interface UniqueOddSpecifiers {

    String message() default "Duplicate odd specifiers are not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

