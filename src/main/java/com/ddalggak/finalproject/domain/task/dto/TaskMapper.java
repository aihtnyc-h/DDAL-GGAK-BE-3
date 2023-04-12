package com.ddalggak.finalproject.domain.task.dto;

import static com.ddalggak.finalproject.domain.ticket.entity.TicketStatus.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.ddalggak.finalproject.domain.label.dto.LabelMapper;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;

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

	// @Named("countOfMovementTicket")
	// default int countOfMovementTicket(Task task) {
	// 	return (int)task.getTicketList().stream().filter(ticket -> ticket.movementTicket == IN_PROGRESS).count();
	// }
}
