package itmo.help_interview.bot.exceptions;

public class LanguageTagNotFoundException extends RuntimeException {

	public LanguageTagNotFoundException() {
	}

	public LanguageTagNotFoundException(String message) {
		super(message);
	}
}
