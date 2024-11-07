package itmo.help_interview.bot.service.handlers;

import java.util.List;
import java.util.stream.Collectors;

import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
class AboutMeCommandHandler implements CommandHandler {

    private final UserRepository userRepository;

    @Override
    public void handle(TelegramBot bot, Update update) {
        long chatId = update.getMessage().getChatId();
        var tags = userRepository.findById(chatId)
                .map(User::getTags)
                .orElse(List.of());
        var text = tags.isEmpty()
                ? "У тебя нет тегов, попробуй добавить из через /settings!"
                : tags.stream().map(Tag::getName).collect(Collectors.joining(", "));
        bot.send(chatId, text);
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
