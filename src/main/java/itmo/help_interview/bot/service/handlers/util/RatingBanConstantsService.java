package itmo.help_interview.bot.service.handlers.util;

import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingBanConstantsService {
	public static int MODERATION_RATING = 5;
	public static int QUESTION_MAKER_RATING = 3;

	private final UserService userService;

	public boolean isUserBanned(long chatId) {
		return userService.getUserById(chatId).isBanned();
	}

	public boolean userAllowToCreateQuestions(long chatId) {
		return userService.getUserById(chatId).getRating() > QUESTION_MAKER_RATING;
	}

	public void computeNewUserRatingAfterHisAnswer(long chatId, boolean isCorrect, Tag difficultTag) {
		User user = userService.getUserById(chatId);
		int oldRating = user.getRating();

		int additionRating = 0;
		if (isCorrect) {
			switch (difficultTag.getName()) {
				case "Junior":
					additionRating = 1;
					break;
				case "Middle":
					additionRating = 2;
					break;
				case "Senior":
					additionRating = 3;
					break;
			}
		} else {
			if (oldRating > 0) {
				additionRating = -1;
			}
		}
		int newRating = oldRating += additionRating;

		userService.updateUserRating(user, newRating);
	}

	public boolean userAllowToModerateQuestions(long chatId) {
		return userService.getUserById(chatId).getRating() > MODERATION_RATING;
	}


}
