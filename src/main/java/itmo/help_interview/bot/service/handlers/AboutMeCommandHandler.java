package itmo.help_interview.bot.service.handlers;

import java.util.stream.Collectors;

import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import itmo.help_interview.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
class AboutMeCommandHandler implements CommandHandler {

    private final UserService userService;

    @Override
    public void handle(TelegramBot bot, Update update) {
        long chatId = update.getMessage().getChatId();
        var user = userService.getUserById(chatId);
        var tags = user.getTags();

        StringBuilder answerAboutUser = new StringBuilder();
        // Добавляем теги пользователя
        var text = tags.isEmpty()
                ? "У тебя нет тегов, попробуй добавить из через /settings!"
                : "Ваши теги (категории): " + tags.stream().map(Tag::getName).collect(Collectors.joining(", "));
        answerAboutUser.append(text);
        answerAboutUser.append("\n");
        // Добавляем информацию о рейтинге
        var rating = user.getRating();
        answerAboutUser.append("Ваш рейтинг: ").append(rating);
        // Выводим плачевную информацию пользователю если он в бане
        if (user.isBanned()) {
            answerAboutUser.append("\n\nВЫ ЗАБАНЕНЫ! Вы не можете добавлять свои вопросы и модерировать чужие");
        }

        bot.send(chatId, answerAboutUser.toString());
    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {
        //
    }

    @Override
    public String getCommandName() {
        return "/about_me";
    }
}
