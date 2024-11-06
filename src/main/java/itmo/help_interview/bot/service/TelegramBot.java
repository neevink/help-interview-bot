package itmo.help_interview.bot.service;

import itmo.help_interview.bot.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

	@Value("${bot.name}")
	private String botName;

	@Value("${bot.token}")
	private String token;

	private final HandleDispatcher handleDispatcher;

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
		var isCommand = CommonUtil.extractText(update)
				.map(CommonUtil::startsWithCommand)
				.orElse(false);
		if (isCommand) {
			handleDispatcher.dispatchCommand(this, update);
		} else {
			// код не про команды надо будет поселить тут
			throw new RuntimeException();
		}
	}

	public void send(long chatId, String textToSend) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(String.valueOf(chatId));
		sendMessage.setText(textToSend);
		try {
			execute(sendMessage);
		} catch (TelegramApiException ignored) {
		}
	}

	public void send(SendMessage sendMessage) {
		try {
			execute(sendMessage);
		} catch (TelegramApiException ignored) {
		}
	}

}
