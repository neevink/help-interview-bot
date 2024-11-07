package itmo.help_interview.bot.service.handlers.util;

import itmo.help_interview.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingConstantsService {
	public static int MODERATION_RATING = 50;
	public static int QUESTION_MAKER_RATING = 20;

	private final UserService userService;

	public boolean userAllowToCreateQuestions(long chatId) {
		return userService.getUserById(chatId).getRating() > QUESTION_MAKER_RATING;
	}
}
