package com.ddalggak.finalproject.domain.ticket.dto;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.user.entity.User;

@SpringBootTest
@Transactional
class TicketMapperTest {
	@Autowired
	EntityManager em;
	TicketMapper ticketMapper = Mappers.getMapper(TicketMapper.class);

	@Test
	public void ticketToDtoTest() {
		//given
		User user1 = mock(User.class);
		when(user1.getEmail()).thenReturn("pengooseDev@daum.net");
		Label label1 = mock(Label.class);
		when(label1.getLabelId()).thenReturn(1L);
		when(label1.getLabelTitle()).thenReturn("testLabel");
		when(label1.getLabelLeader()).thenReturn("pengooseDev@daum.net");

		Ticket ticket = mock(Ticket.class);
		when(ticket.getTicketId()).thenReturn(1L);
		when(ticket.getTicketTitle()).thenReturn("testTicket");
		when(ticket.getTicketDescription()).thenReturn("testTicketDescription");
		when(ticket.getStatus()).thenReturn(TicketStatus.TODO);
		when(ticket.getUser()).thenReturn(user1);
		// 일부러 ticket.assigned를 설정 안함
		when(ticket.getExpiredAt()).thenReturn(null);
		when(ticket.getCompletedAt()).thenReturn(null);

		//when
		TicketResponseDto ticketResponseDto = ticketMapper.toDto(ticket);

		//then
		assertThat(ticketResponseDto.getTicketId()).isEqualTo(ticket.getTicketId());
		assertThat(ticketResponseDto.getTitle()).isEqualTo("testTicket");
		assertThat(ticketResponseDto.getAssigned()).isEqualTo(null);
		Assertions.assertNull(ticketResponseDto.getCommentList(), "commentList should be null");

	}

	@Test
	public void speedMapStruct() {
		//given
		Ticket ticket = mock(Ticket.class);
		for (int i = 0; i < 100; i++) {
			Comment comment = mock(Comment.class);
			ticket.getComment().add(comment);
		}

		//when
		long startMapstruct = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			TicketResponseDto ticketResponseDto = ticketMapper.toDto(ticket);
		}
		long takenTimeWithMapStruct = System.currentTimeMillis() - startMapstruct;

		//then

		System.out.println("MapStruct로 걸린 시간: " + takenTimeWithMapStruct);

	}

	@Test
	public void speedWithManual() {
		//given
		Ticket ticket = mock(Ticket.class);
		for (int i = 0; i < 100; i++) {
			Comment comment = mock(Comment.class);
			ticket.getComment().add(comment);
		}
		//when
		long startManual = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			TicketResponseDto ticketResponseDto = new TicketResponseDto(ticket);
		}
		long takenTimeWithManual = System.currentTimeMillis() - startManual;

		//then
		System.out.println("Manual로 걸린 시간: " + takenTimeWithManual);
	}

}