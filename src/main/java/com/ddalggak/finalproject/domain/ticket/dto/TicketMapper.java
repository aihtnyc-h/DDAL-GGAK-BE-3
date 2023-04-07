package com.ddalggak.finalproject.domain.ticket.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ddalggak.finalproject.domain.comment.dto.CommentMapper;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface TicketMapper {

	@Mappings({
		@Mapping(target = "ticketId", source = "ticket.ticketId"),
		@Mapping(target = "title", source = "ticket.ticketTitle"),
		@Mapping(target = "description", source = "ticket.ticketDescription"),
		@Mapping(target = "status", source = "ticket.status"),
		@Mapping(target = "priority", source = "ticket.priority"),
		@Mapping(target = "difficulty", source = "ticket.difficulty"),
		@Mapping(target = "assigned", source = "ticket.user.email"),
		@Mapping(target = "expiredAt", source = "ticket.expiredAt"),
		@Mapping(target = "completedAt", source = "ticket.completedAt"),
		@Mapping(target = "label", source = "ticket.label.labelTitle"),
		@Mapping(target = "commentList", source = "ticket.comment")
	})
	TicketResponseDto toDto(Ticket ticket);

	List<TicketResponseDto> toDtoList(List<Ticket> tickets);
}
