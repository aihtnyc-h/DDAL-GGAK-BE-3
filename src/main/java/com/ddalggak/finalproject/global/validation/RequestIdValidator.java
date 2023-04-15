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
		if (value == null || value < 0) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalid RequestId")
				.addConstraintViolation();
			return false;
		}
		return true;
	}
}
