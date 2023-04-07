package com.ddalggak.finalproject.domain.user.repository;

import java.util.List;
import java.util.Map;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;

public interface UserRepositoryCustom {

	List<DateTicket> getCompletedTicketCountByDate(TicketSearchCondition condition, Long userId);

	Map<TicketStatus, List<TicketResponseDto>> getTicketByUserId(TicketSearchCondition condition, Long userId);

	List<UserResponseDto> getUserFromLabel(Long labelId);

}
