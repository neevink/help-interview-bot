package itmo.help_interview.bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    void handle(TelegramBot bot, Update update);
    void handleCallback(TelegramBot bot, Update update);
    String getCommandName();
}
