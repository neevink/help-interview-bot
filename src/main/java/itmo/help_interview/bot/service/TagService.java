package itmo.help_interview.bot.service;

import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.TagCategory;
import itmo.help_interview.bot.exceptions.DifficultTagNotFoundException;
import itmo.help_interview.bot.exceptions.LanguageTagNotFoundException;
import itmo.help_interview.bot.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TagService {
	private final TagRepository tagRepository;

	public Tag getDifficultTagFromList(List<Tag> tags) {
		return tags.stream()
				.filter(tag -> tag.getCategory() == TagCategory.DIFFICULTY)
				.findFirst()
				.orElseThrow(DifficultTagNotFoundException::new);
	}

	public Tag getLanguageTagFromList(List<Tag> tags) {
		return tags.stream()
				.filter(tag -> tag.getCategory() == TagCategory.LANGUAGE)
				.findFirst()
				.orElseThrow(LanguageTagNotFoundException::new);
	}

	public void addTagForQuestionByTagName(Question question, String tagName) {
		// Ищем тег по имени или создаем новый
		Tag tag = findTagByNameOrThrow(tagName);

		List<Tag> questionTags = question.getTags();
		questionTags.add(tag);
	}

	public Tag findTagByNameOrThrow(String tagName) {
		// Ищем тег по имени
		Optional<Tag> tagOptional = tagRepository.findByName(tagName);
		if (tagOptional.isPresent()) {
			return tagOptional.get();
		} else {
			throw new RuntimeException("Tag not found");
		}
	}
}
