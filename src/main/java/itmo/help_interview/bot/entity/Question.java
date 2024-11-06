package itmo.help_interview.bot.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder()
@Table(name = "question")
public class Question {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "checked", nullable = false)
    private boolean checked = false;
    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "is_open", nullable = false)
    private boolean isOpen = false;
    @Column(name = "comment")
    private String comment = null;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
    @Column(name = "user_id", nullable = false)
    private long userId;
    @Column(name = "right_answer", nullable = false)
    private String rightAnswer;
    @Column(name = "b_answer", nullable = false)
    private String bAnswer;
    @Column(name = "c_answer", nullable = false)
    private String cAnswer;
    @Column(name = "d_answer", nullable = false)
    private String dAnswer;
}
