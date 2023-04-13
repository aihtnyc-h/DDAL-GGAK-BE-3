package com.ddalggak.finalproject.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddalggak.finalproject.domain.review.dto.ReviewCommentRequestDto;
import com.ddalggak.finalproject.domain.review.entity.Review;
import com.ddalggak.finalproject.domain.review.entity.ReviewComment;
@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
	List<ReviewComment> findByReviewOrderByCreatedAtDesc(Review review);
}
