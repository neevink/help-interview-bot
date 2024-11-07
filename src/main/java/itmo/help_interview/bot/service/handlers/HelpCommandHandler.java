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
public class HelpCommandHandler implements CommandHandler {
    @Override
    public void handle(TelegramBot bot, Update update) {
        long chatId = update.getMessage().getChatId();
        var textToSend = "Список доступных команд: " +
                "\n/help — вывести информацию по всем командам" +
                "\n/start — запустить чат-бота" +
                "\n/settings — настройка фильтра вопросов" +
                "\n/get_question — получить следующий вопрос по подготовке к интервью" +
                "\n/add_question — создать новый вопрос";
        bot.send(chatId, textToSend);
    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {

    }

    @Override
    public String getCommandName() {
        return "/help";
    }
}
