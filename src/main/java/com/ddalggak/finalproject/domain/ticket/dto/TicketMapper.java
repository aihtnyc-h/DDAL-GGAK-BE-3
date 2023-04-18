package com.ddalggak.finalproject.domain.ticket.dto;

import static com.ddalggak.finalproject.domain.ticket.entity.TicketStatus.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.ddalggak.finalproject.domain.comment.dto.CommentMapper;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface TicketMapper {

	@Mappings({
		@Mapping(target = "ticketId", source = "entity.ticketId"),
		@Mapping(target = "title", source = "entity.ticketTitle"),
		@Mapping(target = "description", source = "entity.ticketDescription"),
		@Mapping(target = "priority", source = "entity.priority"),
		@Mapping(target = "difficulty", source = "entity.difficulty"),
		@Mapping(target = "assigned", source = "entity.user.nickname"),
		@Mapping(target = "expiredAt", source = "entity.expiredAt"),
		@Mapping(target = "label", source = "entity.label.labelTitle"),
		@Mapping(target = "commentList", source = "entity.comment"),
		@Mapping(target = "progress", expression = "java(checkProgress(entity))"),
		@Mapping(target = "score", expression = "java(calculateScore(entity))")
	})
	TicketResponseDto toDto(Ticket entity);

	default Map<TicketStatus, List<TicketResponseDto>> toDtoMapWithStatus(List<Ticket> tickets) {
		Map<TicketStatus, List<TicketResponseDto>> ticketList = new LinkedHashMap<>() {
			{
				put(TODO, new ArrayList<>());
				put(IN_PROGRESS, new ArrayList<>());
				put(REVIEW, new ArrayList<>());
				put(DONE, new ArrayList<>());
			}
		};
		tickets.stream()
			.map(this::toDto)
			.forEach(ticket -> {
				ticketList.get(ticket.getStatus()).add(ticket);
			});
		return ticketList;
	}

	@Named("checkProgress")
	default double checkProgress(Ticket ticket) {
		LocalDateTime currentDate = LocalDateTime.now();
		if (ticket.getExpiredAt() == null) {
			return 0.0d;
		} else if (ticket.getCompletedAt() != null ||
			currentDate.isAfter(ticket.getExpiredAt().plusDays(1).atStartOfDay())) {
			return 100.0d;
		} else {
			long now = ChronoUnit.HOURS.between(ticket.getCreatedAt(), currentDate);
			long total = ChronoUnit.HOURS.between(ticket.getCreatedAt(),
				ticket.getExpiredAt().plusDays(1).atStartOfDay());
			return (100 * now / (double)total);
		}
	}

	/*
	 * 1. 티켓 점수 산정 방식은 가중 선형 조합을 이용한다.
	 * 2. 추후 상용화 시 abstract class로 돌리고 @Value를 이용해 yml파일로 가중치를 따로 관리한다.
	 * 3. ticket에 달린 task 사라지면 ticket의 score도 바꾼다.
	 */
	@Named("calculateScore")
	default double calculateScore(Ticket ticket) {
		Task task = ticket.getTask();
		if (ticket.getCompletedAt() == null) {
			return 0;
		} else {
			double ticketProgress = ticket.getExpiredAt() == null ? 0.0d : checkTicketDuration(ticket);
			double taskProgress = task.getExpiredAt() == null ? 0.0d : checkTicketDurationWithTask(ticket);

			double difficultyWeight = 4;
			double priorityWeight = 4;
			double ticketProgressWeight = 1;
			double taskProgressWeight = 1;

			return (ticket.getDifficulty() / (double)task.getTotalDifficulty()) * difficultyWeight +
				(ticket.getPriority() / (double)task.getTotalPriority()) * priorityWeight +
				ticketProgress * ticketProgressWeight +
				taskProgress * taskProgressWeight;
		}
	}

	/*
	 * 1. 티켓과 task의 expiredAt의 자유도를 고려한다.
	 */

	default double checkTicketDuration(Ticket ticket) {
		if (ticket.getCompletedAt() == null) {
			return 0;
		} else {
			long now = ChronoUnit.MINUTES.between(ticket.getCreatedAt(), ticket.getCompletedAt());
			long total = ChronoUnit.MINUTES.between(ticket.getCreatedAt(),
				ticket.getExpiredAt().plusDays(1).atStartOfDay());
			return (100 * now / (double)total);
		}
	}

	default double checkTicketDurationWithTask(Ticket ticket) {
		if (ticket.getCompletedAt() == null) {
			return 0;
		} else {
			long now = ChronoUnit.MINUTES.between(ticket.getCreatedAt(), ticket.getCompletedAt());
			long total = ChronoUnit.MINUTES.between(ticket.getCreatedAt(),
				ticket.getTask().getExpiredAt().plusDays(1).atStartOfDay());
			return (100 * now / (double)total);
		}
	}

	List<TicketResponseDto> toDtoList(List<Ticket> tickets);
}
