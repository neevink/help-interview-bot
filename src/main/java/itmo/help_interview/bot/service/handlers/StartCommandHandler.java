package itmo.help_interview.bot.service.handlers;

import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
class StartCommandHandler implements CommandHandler {

    @Override
    public void handle(TelegramBot bot, Update update) {
        String userFirstName = update.getMessage().getChat().getFirstName();
        String answer = "Привет, " + userFirstName + ", я бот для поддержки и развития твоего " +
                "технического уровня в IT. Буду отсылать тебе ежедневно вопросы по темам, " +
                "которые ты выберешь";
        long chatId = update.getMessage().getChatId();
        bot.send(chatId, answer);
    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {

    }

    @Override
    public String getCommandName() {
        return "/start";
    }
}
