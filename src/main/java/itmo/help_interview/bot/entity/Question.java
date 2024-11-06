package itmo.help_interview.bot.entity;

import jakarta.persistence.CascadeType;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "question")
public class Question {
	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "text", nullable = false)
	private String text;

	@Column(name = "is_open", nullable = false)
	private Boolean isOpen = false;

	// Вопрос прошёл модерацию?
	@Column(name = "checked", nullable = false)
	private Boolean checked = false;

	@Column(name = "comment")
	private String comment;

	@Column(name = "creation_date", nullable = false)
	private LocalDateTime creationDate;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "question_tags",
			joinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
	)
	private List<Tag> tags;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Answer> answers;


}
