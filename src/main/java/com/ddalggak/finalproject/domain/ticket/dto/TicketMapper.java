package com.ddalggak.finalproject.domain.ticket.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.ddalggak.finalproject.domain.comment.dto.CommentMapper;
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
		@Mapping(target = "assigned", expression = "java(checkAssigned(entity))"),
		@Mapping(target = "expiredAt", source = "entity.expiredAt"),
		@Mapping(target = "label", source = "entity.label.labelTitle"),
		@Mapping(target = "commentList", source = "entity.comment")
	})
	TicketResponseDto toDto(Ticket entity);

	default Map<TicketStatus, List<TicketResponseDto>> toDtoMapWithStatus(List<Ticket> tickets) {
		Map<TicketStatus, List<TicketResponseDto>> ticketList = new HashMap<>() {
			{
				put(TicketStatus.TODO, new ArrayList<>());
				put(TicketStatus.IN_PROGRESS, new ArrayList<>());
				put(TicketStatus.DONE, new ArrayList<>());
			}
		};
		tickets.stream()
			.map(this::toDto)
			.forEach(ticket -> {
				ticketList.get(ticket.getStatus()).add(ticket);
			});
		return ticketList;
	}

	@Named("checkAssigned")
	default String checkAssigned(Ticket ticket) {
		return ticket.getUser() == null ? null : ticket.getUser().getEmail();
	}

	@Named("checkLabel")
	default String checkLabel(Ticket ticket) {
		return ticket.getLabel() == null ? null : ticket.getLabel().getLabelTitle();
	}

	List<TicketResponseDto> toDtoList(List<Ticket> tickets);
}
