package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketRequestDto {
	@Schema(name = "task Id")
	@NotNull(message = "task Id is required")
	private Long taskId;
	@Schema(name = "ticket title", example = "ticket title")
	private String ticketTitle;
	@Schema(name = "ticket Description", example = "ticket Description")
	private String ticketDescription;
	@Schema(name = "ticket priority", example = "ticket priority")
	private int priority;
	@Schema(name = "ticket difficulty", example = "ticket difficulty")
	private int difficulty;
	@Schema(name = "ticket assigned", example = "ticket assigned")
	private String assigned;
	@Schema(name = "when does this project expired at", example = "2023-03-22")
	private LocalDate ticketExpiredAt;
}
