package itmo.help_interview.bot.service;

import itmo.help_interview.bot.config.BotConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@AllArgsConstructor
public class TelegramBotRunner extends TelegramLongPollingBot {

	private final BotConfig botConfig;

	@Override
	public String getBotUsername() {
		return botConfig.getBotName();
	}

	@Override
	public String getBotToken() {
		return botConfig.getToken();
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String messageText = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();
			String userFirstName = update.getMessage().getChat().getFirstName();

			switch (messageText) {
				case "/start":
					startCommandReceived(chatId, userFirstName);
					break;
				default:
					// Отправляем обратно сообщение
					System.out.println("get message from " + userFirstName);
					sendMessage(chatId,
							"Message text: " + messageText +
							"\nYour name: " + userFirstName +
							"\nYour chatId: " + chatId);
			}
		}

	}

	private void startCommandReceived(Long chatId, String name) {
		String answer = "Привет, " + name + ", я бот для поддержки и развития твоего " +
				"технического уровня в IT. Буду отсылать тебе ежедневно вопросы по темам, " +
				"которые ты выберешь";
		sendMessage(chatId, answer);
	}

	private void sendMessage(Long chatId, String textToSend) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(String.valueOf(chatId));
		sendMessage.setText(textToSend);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {

		}
	}

}
