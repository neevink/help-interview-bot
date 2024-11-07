package itmo.help_interview.bot.exceptions;

public class UserNotEnoughRatingException extends RuntimeException {

	public UserNotEnoughRatingException() {
	}

	public UserNotEnoughRatingException(String message) {
		super(message);
	}

}
