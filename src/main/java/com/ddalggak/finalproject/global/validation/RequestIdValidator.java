package com.ddalggak.finalproject.global.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestIdValidator implements ConstraintValidator<RequestId, Long> {

	@Override
	public void initialize(RequestId constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		return value != null && value >= 0;
	}
}
