// package com.ddalggak.finalproject.domain.comment.dto;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import javax.persistence.EntityManager;
//
// import org.junit.jupiter.api.Test;
// import org.mapstruct.factory.Mappers;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.ddalggak.finalproject.domain.comment.entity.Comment;
// import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
// import com.ddalggak.finalproject.domain.user.entity.User;
//
// @SpringBootTest
// @Transactional
// class CommentMapperTest {
//
// 	@Autowired
// 	EntityManager em;
// 	private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
//
// 	@Test
// 	public void commentMapperTest() {
// 		//given
// 		User user1 = mock(User.class);
// 		when(user1.getEmail()).thenReturn("pengooseDev@daum.net");
//
// 		Comment comment = mock(Comment.class);
// 		when(comment.getCommentId()).thenReturn(1L);
// 		when(comment.getComment()).thenReturn("test");
// 		when(comment.getTicket()).thenReturn(mock(Ticket.class));
// 		when(comment.getUser()).thenReturn(user1);
//
// 		//when
// 		CommentResponseDto commentResponseDto = commentMapper.toDto(comment);
//
// 		//then
// 		assertThat(commentResponseDto.getCommentId()).isEqualTo(1L);
// 		assertThat(commentResponseDto.getEmail()).isEqualTo("pengooseDev@daum.net");
//
// 	}
//
// 	@Test
// 	public void toEntityTest() {
// 		//given
// 		User user = mock(User.class);
// 		when(user.getEmail()).thenReturn("pengooseDev@daum.net");
//
// 		CommentRequestDto commentRequestDto = mock(CommentRequestDto.class);
// 		when(commentRequestDto.getComment()).thenReturn(null);
// 		when(commentRequestDto.getTicketId()).thenReturn(1L);
//
// 		Ticket ticket = mock(Ticket.class);
// 		when(ticket.getTicketId()).thenReturn(1L);
// 		when(ticket.getTicketTitle()).thenReturn("test Ticket");
//
// 		//when
// 		Comment comment = commentMapper.mapToEntity(user, ticket, commentRequestDto);
//
// 		//then
// 		assertThat(comment.getComment()).isNull();
// 		assertThat(comment.getTicket().getTicketId()).isEqualTo(1L);
// 		assertThat(comment.getUser().getEmail()).isEqualTo("pengooseDev@daum.net");
// 	}
//
// }