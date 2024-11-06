package itmo.help_interview.bot.service;

import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@AllArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;

	public List<Question> getAllQuestionsContainsBothTags(Tag difficultTag, Tag languageTag) {
		return questionRepository.findAll().stream()
				.filter(q -> new HashSet<>(q.getTags()).containsAll(List.of(difficultTag, languageTag)))
				.toList();
	}

}
