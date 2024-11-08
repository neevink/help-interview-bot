package itmo.help_interview.bot.repository;

import itmo.help_interview.bot.entity.UserQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserQuestionAnswerRepository extends JpaRepository<UserQuestionAnswer, Long> {

	List<UserQuestionAnswer> findAllByUser_chatId(Long chatId);

	List<UserQuestionAnswer> findAllByQuestion_idAndReactionIsNotNull(Long questionId);

	List<UserQuestionAnswer> findAllByUser_chatIdAndQuestion_id(Long chatId, Long questionId);

}
