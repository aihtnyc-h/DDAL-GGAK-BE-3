package com.ddalggak.finalproject.global.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = {RequestIdValidator.class})
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestId {

	String message() default "Null or negative Id is not allowed.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
