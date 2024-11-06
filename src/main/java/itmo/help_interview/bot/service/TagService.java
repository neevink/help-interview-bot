package itmo.help_interview.bot.service;

import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.TagCategory;
import itmo.help_interview.bot.exceptions.DifficultTagNotFoundException;
import itmo.help_interview.bot.exceptions.LanguageTagNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TagService {

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

}
