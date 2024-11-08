package itmo.help_interview.bot.service;

import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.entity.UserQuestionAnswer;
import itmo.help_interview.bot.entity.UserQuestionAnswerReaction;
import itmo.help_interview.bot.repository.QuestionRepository;
import itmo.help_interview.bot.repository.UserQuestionAnswerRepository;
import itmo.help_interview.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserQuestionAnswerService {
	public static final int APPROVE_LIMIT_FROM_MODERS = 1;
	public static final int BLOCK_LIMIT_FROM_MODERS = 1;
	public static final int REPORT_LIMIT_FROM_MODERS = 1;

	private final UserQuestionAnswerRepository userQuestionAnswerRepository;
	private final UserRepository userRepository;
	private final QuestionRepository questionRepository;

	public UserQuestionAnswer updateAndLoadQuestionCheck(UserQuestionAnswer userQuestionAnswer) {
		UserQuestionAnswer updated = userQuestionAnswerRepository.save(userQuestionAnswer);

		// ПОКА ЧТО логика тут, но вообще это должен быть таймер, который в какие-то промежутки времени прогоняет
		// проверку для всех вопросов, что находятся на модерации
		Question question = userQuestionAnswer.getQuestion();
		User user = userQuestionAnswer.getUser();
		if (question.getChecked() || question.getIsDeleted() || user.isBanned()) {
			// Когда данная реакция пришла, вопрос уже был проверен и отведён с модерации
			// В таком случае ничего не делаем, модерация уже проведена
		} else {
			// Очередная итерация проверки -> Получим все ответы на этот вопрос у которых есть оценка и посмотрим на них.
			List<UserQuestionAnswer> allModersAnswers =
					userQuestionAnswerRepository.findAllByQuestion_idAndReactionIsNotNull(question.getId());
			long approveCount = allModersAnswers.stream()
					.filter(ans -> UserQuestionAnswerReaction.APPROVE.equals(ans.getReaction()))
					.count();
			long blockCount = allModersAnswers.stream()
					.filter(ans -> UserQuestionAnswerReaction.BLOCK.equals(ans.getReaction()))
					.count();
			long reportCount = allModersAnswers.stream()
					.filter(ans -> UserQuestionAnswerReaction.REPORT.equals(ans.getReaction()))
					.count();

			if (reportCount >= REPORT_LIMIT_FROM_MODERS) {
				// Бан пользователя и удаление вопроса
				user.setBanned(true);
				question.setIsDeleted(true);
				question.setChecked(true);
			} else if (blockCount >= BLOCK_LIMIT_FROM_MODERS) {
				// Только удаление вопроса
				question.setIsDeleted(true);
				question.setChecked(true);
			} else if (approveCount >= APPROVE_LIMIT_FROM_MODERS) {
				// Допуск вопроса
				question.setChecked(true);
			}

			// Обновляем вопрос и юзера (каждый из них мог измениться)
			userRepository.save(user);
			questionRepository.save(question);
		}

		return updated;
	}


}
