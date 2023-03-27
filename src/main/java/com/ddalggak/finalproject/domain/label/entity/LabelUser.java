package com.ddalggak.finalproject.domain.label.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ddalggak.finalproject.domain.label.dto.LabelUserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "Label_User")
@NoArgsConstructor
@AllArgsConstructor
public class LabelUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "UserId")
	private User user;

	@ManyToOne
	@JoinColumn(name = "LabelId")
	private Label label;

	public static LabelUser create(LabelUserRequestDto labelUserRequestDto) {
		return LabelUser.builder()
			.user(labelUserRequestDto.getUser())
			.build();
	}

	public void addLabel(Label label) {
		this.label = label;
	}
}
