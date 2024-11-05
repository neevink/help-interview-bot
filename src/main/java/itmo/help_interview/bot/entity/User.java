package itmo.help_interview.bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bot_user")
public class User {
	@Id
	@Column(name = "id", nullable = false)
	private long chatId;

	@Column(name = "is_banned", nullable = false)
	private boolean isBanned;

	@Column(name = "rating", nullable = false)
	private long firstName;
}
