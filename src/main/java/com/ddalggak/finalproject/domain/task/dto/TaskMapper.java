package com.ddalggak.finalproject.domain.task.dto;

import static com.ddalggak.finalproject.domain.ticket.entity.TicketStatus.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.ddalggak.finalproject.domain.label.dto.LabelMapper;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.entity.User;

@Mapper(componentModel = "spring", uses = {LabelMapper.class, TicketMapper.class, UserMapper.class})
public interface TaskMapper {

	@Mappings({
		@Mapping(target = "id", source = "entity.taskId"),
		@Mapping(target = "taskTitle", source = "entity.taskTitle"),
		@Mapping(target = "taskLeader", source = "entity.taskLeader"),
		@Mapping(target = "createdAt", source = "entity.createdAt"),
		@Mapping(target = "expiredAt", source = "entity.expiredAt"),
		@Mapping(target = "totalDifficulty", expression = "java(sumOfDifficulty(entity))"),
		@Mapping(target = "totalPriority", expression = "java(sumOfPriority(entity))"),
		@Mapping(target = "progress", expression = "java(calculateProgress(entity))"),
		@Mapping(target = "labels", source = "entity.labelList"),
		@Mapping(target = "tickets", source = "entity.ticketList")
	})
	TaskResponseDto toDto(Task entity);

	@Mappings({
		@Mapping(target = "id", source = "entity.taskId"),
		@Mapping(target = "taskTitle", source = "entity.taskTitle"),
		@Mapping(target = "expiredAt", source = "entity.expiredAt"),
		@Mapping(target = "completedTickets", expression = "java(countOfCompletedTickets(entity))"),
		@Mapping(target = "totalTickets", expression = "java(countOfTotalTicket(entity))"),
		@Mapping(target = "participantsCount", expression = "java(countOfParticipants(entity))"),
		@Mapping(target = "participants", source = "entity.taskUserList")
	})
	TaskBriefResponseDto toBriefDto(Task entity);

	@Mappings({
		@Mapping(target = "id", source = "entity.taskId"),
		@Mapping(target = "taskTitle", source = "entity.taskTitle"),
		@Mapping(target = "taskLeader", source = "entity.taskLeader"),
		@Mapping(target = "createdAt", source = "entity.createdAt"),
		@Mapping(target = "expiredAt", source = "entity.expiredAt"),
		@Mapping(target = "totalDifficulty", expression = "java(sumOfDifficulty(entity))"),
		@Mapping(target = "totalPriority", expression = "java(sumOfPriority(entity))"),
		@Mapping(target = "progress", expression = "java(calculateProgress(entity))"),
		@Mapping(target = "labels", source = "entity.labelList"),
		@Mapping(target = "tickets", source = "entity.ticketList")
	})
	TaskResponseDto toDtoWithUser(Task entity, User user);

	@Named("sumOfDifficulty")
	default int sumOfDifficulty(Task task) {
		return task.getTicketList().stream().mapToInt(Ticket::getDifficulty).sum();
	}

	@Named("sumOfPriority")
	default int sumOfPriority(Task task) {
		return task.getTicketList().stream().mapToInt(Ticket::getPriority).sum();
	}

	@Named("countOfCompletedTickets")
	default int countOfCompletedTickets(Task task) {
		return (int)task.getTicketList().stream().filter(ticket -> ticket.getStatus() == DONE).count();
	}

	@Named("countOfTotalTicket")
	default int countOfTotalTicket(Task task) {
		return task.getTicketList().size();
	}

	@Named("countOfParticipants")
	default int countOfParticipants(Task task) {
		return task.getTaskUserList().size();
	}

	/*
	 * 1. 현재 날짜가 만료일보다 늦으면 100%를 반환한다.
	 * 2. 진행률은 (현재 날짜 - 생성일) / (만료일 - 생성일) * 100을 반환한다.
	 * 3. 진행률 같은 경우, DB에 쌓아놓고 벌크 쿼리 주기적으로 날려주는 것보다는, 가지고 와서 계산하는게 좋을 것 같다.
	 */
	@Named("calculateProgress")
	default double calculateProgress(Task task) {
		LocalDateTime currentDate = LocalDateTime.now();
		if (currentDate.isAfter(task.getExpiredAt().plusDays(1).atStartOfDay())) {
			return 100.0d;
		} else {
			long now = ChronoUnit.HOURS.between(task.getCreatedAt(), currentDate);
			long total = ChronoUnit.HOURS.between(task.getCreatedAt(), task.getExpiredAt().plusDays(1).atStartOfDay());
			return (100 * now / (double)total);
		}
	}

	/* todo
	 * 완료 현황은 (완료된 티켓의 난이도 / 전체 티켓 난이도) * (완료된 티켓의 우선순위 / 전체 티켓 우선순위) * (calculateProgress) * 100을 반환한다.
	 */
	// @Named("calculateCompletion")
	// default double calculateCompletion(Task task) {
	// 	double a = 0;
	// 	double b = 0;
	// 	double c = 0;
	// 	task.getTicketList().stream().filter(ticket -> ticket.getStatus() == DONE).forEach(ticket -> {
	// 		a += ticket.getDifficulty();
	// 		b += ticket.getPriority();
	// 	});
	// }
}
