// package com.ddalggak.finalproject.domain.alram.service;
//
//
// import java.util.List;
// import java.util.stream.Collectors;
//
// import org.springframework.stereotype.Service;
//
// import com.ddalggak.finalproject.domain.alram.entity.Subscription;
// import com.ddalggak.finalproject.domain.alram.repository.SubscriptionRepository;
//
// @Service
// public class SubscriptionService {
// 	private final SubscriptionRepository subscriptionRepository;
// 	public SubscriptionService(SubscriptionRepository subscriptionRepository) {
// 		this.subscriptionRepository = subscriptionRepository;
// 	}
//
// 	public void subscribe(Long reviewId, Long userId) {
// 		subscriptionRepository.save(new Subscription(reviewId, userId));
// 	}
// 	public List<Long> getSubscribedUserIds(Long reviewId) {
// 		List<Subscription> subscriptions = subscriptionRepository.findByReviewId(reviewId);
// 		return subscriptions.stream().map(Subscription::getUserId).collect(Collectors.toList());
// 	}
// }
