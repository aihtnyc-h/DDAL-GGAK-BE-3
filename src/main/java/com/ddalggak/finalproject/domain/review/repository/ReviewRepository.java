package com.ddalggak.finalproject.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddalggak.finalproject.domain.review.dto.ReviewResponseDto;
import com.ddalggak.finalproject.domain.review.entity.Review;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByOrderByModifiedAtDesc();
}
