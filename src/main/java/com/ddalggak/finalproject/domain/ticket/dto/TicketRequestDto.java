package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;

import javax.validation.constraints.Future;

import com.ddalggak.finalproject.global.validation.RequestId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Setter
public class TicketRequestDto {
	@Schema(name = "task Id")
	@RequestId
	private Long taskId;
	@Schema(name = "ticket title", example = "ticket title")
	private String ticketTitle;
	@Schema(name = "ticket Description", example = "ticket Description")
	private String ticketDescription;
	@Schema(name = "ticket priority", example = "ticket priority")
	private int priority;
	@Schema(name = "ticket difficulty", example = "ticket difficulty")
	private int difficulty;
	@Schema(name = "when does this project expired at", example = "2023-03-22")
	@Future
	private LocalDate ticketExpiredAt;
}
