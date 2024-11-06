package itmo.help_interview.bot.repository;

import itmo.help_interview.bot.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

}
