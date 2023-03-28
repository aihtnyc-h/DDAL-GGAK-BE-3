package com.ddalggak.finalproject.global.mail.randomCode;

import org.springframework.data.repository.CrudRepository;

public interface RandomCodeRepository extends CrudRepository<RandomCode, Long> {
	RandomCode findByRandomCode(String randomCode);
}
