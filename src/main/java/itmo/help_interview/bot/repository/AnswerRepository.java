package itmo.help_interview.bot.repository;

import itmo.help_interview.bot.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

}
