package itmo.help_interview.bot.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bot_user")
public class User {
	@Id
	@Column(name = "id", nullable = false)
	private long chatId;

	@Column(name = "is_banned", nullable = false)
	private boolean isBanned;

	@Column(name = "rating", nullable = false)
	private long rating;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_tags",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
	)
	private List<Tag> tags;

	public void addTag(Tag tag) {
		tags.add(tag);
	}
}
