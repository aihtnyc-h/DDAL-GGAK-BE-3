package com.ddalggak.finalproject.domain.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.mapper.GenericMapper;

@Mapper(componentModel = "spring")
public interface CommentMapper extends GenericMapper<CommentResponseDto, Comment> {

	@Override
	@Mappings({
		@Mapping(target = "email", source = "comment.user.email")
	})
	CommentResponseDto toDto(Comment comment);

	@Mappings({
		@Mapping(target = "comment", source = "commentRequestDto.comment"),
		@Mapping(target = "ticket", source = "ticket"),
		@Mapping(target = "user", source = "user")
	})
	Comment mapToEntity(User user, Ticket ticket, CommentRequestDto commentRequestDto);
}