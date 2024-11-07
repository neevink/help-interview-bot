package itmo.help_interview.bot.exceptions;

public class NotEvenSinglePotentialQuestionForUserException extends RuntimeException {

	public NotEvenSinglePotentialQuestionForUserException() {
	}

	public NotEvenSinglePotentialQuestionForUserException(String message) {
		super(message);
	}
}
