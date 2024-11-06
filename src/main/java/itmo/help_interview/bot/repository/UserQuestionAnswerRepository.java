package itmo.help_interview.bot.repository;

import itmo.help_interview.bot.entity.UserQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestionAnswerRepository extends JpaRepository<UserQuestionAnswer, Long> {

}
