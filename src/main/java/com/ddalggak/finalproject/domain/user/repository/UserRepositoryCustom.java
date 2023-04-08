package com.ddalggak.finalproject.domain.user.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.user.entity.User;

public interface UserRepositoryCustom {

	List<DateTicket> getCompletedTicketCountByDate(TicketSearchCondition condition, Long userId);

	List<Ticket> getTicketByUserId(TicketSearchCondition condition, Long userId);

	List<LabelUser> getUserFromLabelId(Long labelId);

	List<TaskUser> getTaskUserFromTaskId(Long taskId);

	List<User> getUserFromTaskId(Long taskId);

}
