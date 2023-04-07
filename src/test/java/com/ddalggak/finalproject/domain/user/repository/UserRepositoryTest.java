package com.ddalggak.finalproject.domain.user.repository;

import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.QDateTicket;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.ticket.service.TicketService;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class UserRepositoryTest {

	@Autowired
	EntityManager em;
	@Autowired
	UserRepository userRepository;

	@Autowired
	TicketService ticketService;

	JPAQueryFactory queryFactory;

	@Test
	public void myTicketPageTest() {
		//given
		User pengoose = User.builder() // user 설정
			.email("kimdaeHyun1234@daum.net")
			.nickname("pengoose")
			.password("12345678@!A")
			.role(UserRole.USER)
			.build();
		em.persist(pengoose);

		for (int i = 0; i < 365; i++) { //
			// TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
			// 	.ticketTitle("number")
			// 	.ticketExpiredAt(LocalDate.MAX)
			// 	.ticketDescription("testTicket")
			// 	.build();
			// Ticket ticket = Ticket.builder()
			// 	.ticketRequestDto(ticketRequestDto)
			// 	.build();
			// em.persist(ticket);
			// ticketService.completeTicket(pengoose, ticket.getTicketId());
			Ticket ticket = mock(Ticket.class);
			em.persist(ticket);
			when(ticket.getCompletedAt()).thenReturn(LocalDate.now().minusDays(i));
		}
		//when
		LocalDate start = LocalDate.now();

		List<DateTicket> result = queryFactory
			.select(new QDateTicket(
				ticket.completedAt.as("date"),
				ticket.count().as("completedTicket")
			))
			.from(ticket)
			.where(
				ticket.user.userId.eq(pengoose.getUserId())
					.and(ticket.completedAt.between(start.minusYears(1), start))
					.and(ticket.status.eq(TicketStatus.DONE))
			)
			.groupBy(ticket.completedAt)
			.orderBy(ticket.completedAt.asc())
			.fetch();

		//then
		assertThat(result.size()).isEqualTo(365);

	}

}