
package itmo.help_interview.bot.service.handlers;

import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.TagCategory;
import itmo.help_interview.bot.repository.TagRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import itmo.help_interview.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class SettingsCommandHandler implements CommandHandler {
    private final TagRepository tagRepository;
    private final UserService userService;

    @Override
    public void handle(TelegramBot bot, Update update) {
        String answer = "По какой технологии ты хочешь получать вопросы?";
        long chatId = update.getMessage().getChatId();

        // Получаем список тегов из репозитория с фильтром по технологиям (языкам)
        List<Tag> tags = tagRepository.findAll().stream()
                .filter(tag -> TagCategory.LANGUAGE.equals(tag.getCategory()))
                .toList();


        // Создаем InlineKeyboardMarkup
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Создаем кнопки для каждого тега
        for (Tag tag : tags) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(tag.getName());
            button.setCallbackData("/settings_language_" + tag.getName()); // Используем текст тега в качестве callback data
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        // Отправляем сообщение с клавиатурой
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(answer);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.execute(message); // Убедитесь, что ваш метод bot.send() поддерживает отправку SendMessage с клавиатурой
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createNewKeyboard() {
        // Получаем список тегов из репозитория с фильтром по сложностям
        List<Tag> tags = tagRepository.findAll().stream()
                .filter(tag -> TagCategory.DIFFICULTY.equals(tag.getCategory()))
                .toList();


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (Tag tag : tags) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(tag.getName());
            button.setCallbackData("/settings_difficulty_" + tag.getName()); // Используем текст тега в качестве callback data
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {
        String callbackData = update.getCallbackQuery().getData();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        switch (getPanelNameFromCallbackData(callbackData)){
            case "language":
                userService.addTagToUser(chatId, getPressedButtonFromCallbackData(callbackData));

                // Подготовка нового текста и клавиатуры
                String newText = "Вы выбрали технологию для изучения: " + getPressedButtonFromCallbackData(callbackData) + ". На какую позицию вы претендуете?";
                InlineKeyboardMarkup newKeyboard = createNewKeyboard();

                // Создание EditMessageText для замены текста и клавиатуры
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(messageId);
                editMessage.setText(newText);
                editMessage.setReplyMarkup(newKeyboard);

                // Выполнение команды изменения сообщения
                try {
                    bot.execute(editMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "difficulty":
                userService.addTagToUser(chatId, getPressedButtonFromCallbackData(callbackData));

                EditMessageText editMessageNew = new EditMessageText();
                editMessageNew.setChatId(chatId);
                editMessageNew.setMessageId(messageId);
                editMessageNew.setText("Вы выбрали уровень: " + getPressedButtonFromCallbackData(callbackData) + " . Спасибо, ваши настройки сохранены.");

                // Выполнение изменения сообщения
                try {
                    bot.execute(editMessageNew);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private String getPanelNameFromCallbackData(String callbackData){
        return callbackData.split("_")[1];
    }

    private String getPressedButtonFromCallbackData(String callbackData){
        return callbackData.split("_")[2];
    }

    @Override
    public String getCommandName() {
        return "/settings";
    }
}
