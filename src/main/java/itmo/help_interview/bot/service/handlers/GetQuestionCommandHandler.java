package itmo.help_interview.bot.service.handlers;

import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработка команды получения вопроса /get_question
 */
@Component
@RequiredArgsConstructor
public class GetQuestionCommandHandler implements CommandHandler {

	private final UserRepository userRepository;

	@Override
	public void handle(TelegramBot bot, Update update) {
		String messageText = update.getMessage().getText();
		long chatId = update.getMessage().getChatId();
		String userFirstName = update.getMessage().getChat().getFirstName();
		var textToSend = "Message text: " + messageText +
				"\nYour name: " + userFirstName +
				"\nYour chatId: " + chatId +
				"All users = " + userRepository.findAll().stream().findFirst();
		bot.send(chatId, textToSend);
	}

	@Override
	public String getCommandName() {
		return "/get_question";
	}
}
