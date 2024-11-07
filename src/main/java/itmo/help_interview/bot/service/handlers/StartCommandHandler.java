package itmo.help_interview.bot.service.handlers;

import java.util.List;

import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    private final UserRepository userRepository;

    @Override
    public void handle(TelegramBot bot, Update update) {
        long chatId = update.getMessage().getChatId();
        if (userRepository.findById(chatId).isEmpty()) {
            userRepository.save(
                    User.builder()
                            .chatId(chatId)
                            .isBanned(false)
                            .rating(0)
                            .tags(List.of())
                            .build()
            );
        }
        String userFirstName = update.getMessage().getChat().getFirstName();
        String answer = "Привет, " + userFirstName + ", я бот для поддержки и развития твоего " +
                "технического уровня в IT. Буду отсылать тебе ежедневно вопросы по темам, " +
                "которые ты выберешь";
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
