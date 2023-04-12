package com.ddalggak.finalproject.domain.alram.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subscription")
public class Subscription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long subscriptionId;
	@Column(name = "reivew_id")
	private Long reviewId;
	@Column(name = "user_id")
	private Long userId;
	public Subscription() {

	}
	public Subscription(Long reviewId, Long userId) {
		this.reviewId = reviewId;
		this.userId = userId;
	}
}
