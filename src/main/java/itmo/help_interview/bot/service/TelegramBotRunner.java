package itmo.help_interview.bot.service;

import itmo.help_interview.bot.repository.QuestionRepository;
import itmo.help_interview.bot.repository.TagRepository;
import itmo.help_interview.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBotRunner extends TelegramLongPollingBot {

	@Value("${bot.name}")
	private String botName;

	@Value("${bot.token}")
	private String token;

	private final UserRepository userRepository;
	private final TagRepository tagRepository;
	private final QuestionRepository questionRepository;

	@Override
	public String getBotUsername() {
		return botName;
	}

	@Override
	public String getBotToken() {
		return token;
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
				case "/question":
					System.out.println("get /question from " + userFirstName);
					sendMessage(chatId,
							"Random question: " + questionRepository.findAll().stream().findAny().get());
					break;
				default:
					// Отправляем обратно сообщение
					System.out.println("get message from " + userFirstName);
					sendMessage(chatId,
							"Message text: " + messageText +
							"\nYour name: " + userFirstName +
							"\nYour chatId: " + chatId +
							"\nПервый юзер = " + userRepository.findAll().stream().findFirst().get() +
							"\nПервый тег = " + tagRepository.findAll().stream().findFirst().get()

					);
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
