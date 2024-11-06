package itmo.help_interview.bot.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import itmo.help_interview.bot.CommonUtil;
import itmo.help_interview.bot.service.handlers.DefaultCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HandleDispatcher {
    private final Map<String, CommandHandler> handlers;
    private final DefaultCommandHandler defaultCommandHandler;

    public HandleDispatcher(List<CommandHandler> handlers, DefaultCommandHandler defaultCommandHandler) {
        this.defaultCommandHandler = defaultCommandHandler;
        this.handlers = handlers.stream()
                .filter(it -> it != defaultCommandHandler)
                .collect(Collectors.toMap(CommandHandler::getCommandName, Function.identity()));
    }

    public void dispatchCommand(TelegramBot bot, Update update) {
        CommonUtil.extractText(update)
                .flatMap(CommonUtil::extractCommand)
                .map(handlers::get)
                .orElse(defaultCommandHandler)
                .handle(bot, update);
    }

    public void dispatchCallback(TelegramBot bot, Update update) {
        String callbackData = update.getCallbackQuery().getData();
        for (String handlerName : handlers.keySet()){
            if (callbackData.startsWith(handlerName)){
                handlers.get(handlerName).handleCallback(bot, update);
            }
        }
    }
}
