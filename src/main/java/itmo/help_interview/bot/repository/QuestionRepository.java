package itmo.help_interview.bot.repository;

import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findAllByTagsContaining(Tag tag);

}
