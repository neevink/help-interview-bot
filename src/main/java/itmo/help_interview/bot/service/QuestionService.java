package itmo.help_interview.bot.service;

import itmo.help_interview.bot.entity.Answer;
import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.exceptions.UserNotFoundException;
import itmo.help_interview.bot.repository.AnswerRepository;
import itmo.help_interview.bot.repository.QuestionRepository;
import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.handlers.util.NewQuestionContext;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Component
@AllArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final UserRepository userRepository;
	private final AnswerRepository answerRepository;

	public List<Question> getAllQuestionsContainsBothTags(Tag difficultTag, Tag languageTag) {
		return questionRepository.findAll().stream()
				.filter(q -> new HashSet<>(q.getTags()).containsAll(List.of(difficultTag, languageTag)))
				.toList();
	}

	public Question getQuestionById(Long id) {
		return questionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Question not found"));
	}

	@Transactional
	public Question saveNewQuestionByNewQuestionContext(NewQuestionContext questionContext) {
		Question newQuestion = questionContext.getQuestion();
		Long chatId = questionContext.getChatId();
		newQuestion.setUser(userRepository.findById(chatId)
				.orElseThrow(() ->
						new UserNotFoundException("Не найден Ваш юзер в бд, попробуйте регистрацию командой /start")
				));

		newQuestion.setId(null);
		// TODO при добавлении открытых вопросов исправить тут. (Поле должно заполняться из обработки контекста)
		newQuestion.setIsOpen(false);
		newQuestion.setChecked(false);
		newQuestion.setCreationDate(LocalDateTime.now());
		newQuestion.setIsDeleted(false);

		// Уже заполнено
		List<Answer> answers = newQuestion.getAnswers();
		newQuestion.setAnswers(null);
		Question savedQuestion = questionRepository.save(newQuestion);

		for (Answer answer : answers) {
			answer.setQuestion(newQuestion);
		}
		answers = answers.stream()
				.map(answerRepository::save)
				.toList();
		savedQuestion.setAnswers(answers);

		return savedQuestion;
	}

}
