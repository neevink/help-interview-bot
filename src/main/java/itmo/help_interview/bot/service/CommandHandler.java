package itmo.help_interview.bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    // Будет вызван при отправке команды, например /sample
    void handle(TelegramBot bot, Update update);
    // Будет вызван при колбэке при нажатии на кнопку, например /sample_panel_button
    void handleCallback(TelegramBot bot, Update update);
    String getCommandName();
}
