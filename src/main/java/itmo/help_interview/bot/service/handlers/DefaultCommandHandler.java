package itmo.help_interview.bot.service.handlers;

import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Дефолтный хендлер вызывается, когда не подошел никакой другой.
 */
@Component
@RequiredArgsConstructor
public class DefaultCommandHandler implements CommandHandler {
    private final UserRepository userRepository;

    @Override
    public void handle(TelegramBot bot, Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        var textToSend = "К сожалению, я не понял команду " + messageText + ". Введи команду /help, чтобы увидеть что я умею.";
        bot.send(chatId, textToSend);
    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {

    }

    @Override
    public String getCommandName() {
        return null;
    }
}
