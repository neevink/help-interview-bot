package itmo.help_interview.bot.config;

import itmo.help_interview.bot.service.TelegramBotRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

	private final TelegramBotRunner telegramBot;

	@Autowired
	public BotInitializer(TelegramBotRunner telegramBot) {
		this.telegramBot = telegramBot;
	}

	@EventListener({ContextRefreshedEvent.class})
	public void init() throws TelegramApiException {
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		try {
			telegramBotsApi.registerBot(telegramBot);
		} catch (TelegramApiException e) {

		}
	}
}