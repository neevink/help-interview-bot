package itmo.help_interview.bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
	@Id
	@NonNull
	@Column(name = "id", nullable = false)
	private Integer chatId;

	@NonNull
	@Column(name = "is_banned", nullable = false)
	private Boolean isBanned;

	@NonNull
	@Column(name = "rating", nullable = false)
	private Integer firstName;
}
