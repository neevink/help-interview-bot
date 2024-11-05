package itmo.help_interview.bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tag")
public class Tag {
	@Id
	@Column(name = "id", nullable = false)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private TagCategory category;

	@Column(name = "name", nullable = false)
	private String name;

}
