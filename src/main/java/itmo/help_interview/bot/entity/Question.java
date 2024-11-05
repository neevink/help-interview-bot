package itmo.help_interview.bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "question")
public class Question {
	@Id
	@NonNull
	@Column(name = "id", nullable = false)
	private Integer id;
	@NonNull
	@Column(name = "is_banned", nullable = false)
	private Boolean isBanned;
	@NonNull
	@Column(name = "text")
	private String text;
	@NonNull
	@Column(name = "is_open", nullable = false)
	private Boolean is_open;
	@NonNull
	@Column(name = "comment")
	private String comment;
	@NonNull
	@Column(name = "creation_date")
	private LocalDateTime creationDate;
	@NonNull
	@Column(name = "is_deleted", nullable = false)
	private Boolean is_deleted;
	@NonNull
	@Column(name = "user_id")
	private Integer userId;
}

