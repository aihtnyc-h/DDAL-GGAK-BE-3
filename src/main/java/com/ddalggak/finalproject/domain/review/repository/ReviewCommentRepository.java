package com.ddalggak.finalproject.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddalggak.finalproject.domain.review.entity.ReviewComment;
@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
