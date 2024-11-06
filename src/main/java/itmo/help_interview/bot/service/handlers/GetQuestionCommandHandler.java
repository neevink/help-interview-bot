package itmo.help_interview.bot.service.handlers;

import itmo.help_interview.bot.entity.Answer;
import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.exceptions.NotEvenSinglePotentialQuestionForUserException;
import itmo.help_interview.bot.exceptions.SettingsNotDefinedYetException;
import itmo.help_interview.bot.exceptions.UserNotFoundException;
import itmo.help_interview.bot.repository.QuestionRepository;
import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.QuestionService;
import itmo.help_interview.bot.service.TagService;
import itmo.help_interview.bot.service.TelegramBot;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Обработка команды получения вопроса /get_question
 */
@Component
@RequiredArgsConstructor
public class GetQuestionCommandHandler implements CommandHandler {

    private final UserRepository userRepository;

    private final TagService tagService;
    private final QuestionService questionService;

    private static Random rnd = new Random(42);
    private final QuestionRepository questionRepository;

    @Override
    public void handle(TelegramBot bot, Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        // Логика выборки вопроса
        Question questionForAsk;
        try {
            questionForAsk = getRandomQuestionForUser(chatId);
        } catch (UserNotFoundException e) {
            bot.send(chatId, "Ошибка получения вопроса, попробуйте позже");
            return;
        } catch (SettingsNotDefinedYetException e) {
            bot.send(chatId, "Сначала Вам надо заполнить теги предпочтений");
            return;
        } catch (NotEvenSinglePotentialQuestionForUserException e) {
            bot.send(chatId, e.getMessage());
            return;
        }

        // Логика подачи вопроса пользователю
        StringBuilder textToSend = new StringBuilder();
        textToSend.append("Вопрос: ").append(questionForAsk.getText()).append("\n\nОтветы:\n");
        List<Answer> answers = questionForAsk.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            Answer current = answers.get(i);
            textToSend.append(i + 1).append(". ").append(current.getText()).append("\n");
        }


        // допилить сюда InlineKeyboard в ответы по количеству кнопок

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 0; i < answers.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i + 1)); // Текст кнопки (например, "1", "2" и т.д.)
            button.setCallbackData("/get_question_" + i + "_" + questionForAsk.getId()); // Данные обратного вызова

            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        markup.setKeyboard(rowsInline);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend.toString());
        message.setReplyMarkup(markup);
        bot.send(message);


    }


    @Override
    public String getCommandName() {
        return "/get_question";
    }

    private Question getRandomQuestionForUser(long chatId) {
        List<Tag> userTags;
        // Поиск юзера и ошибка если не нашли его (что странно, юзер должен создаваться
        // при первом обращении от него (МОЖЕТ быть сделать создание юзера в таком случае)
        userTags = userRepository
                .findById(chatId)
                .orElseThrow(UserNotFoundException::new)
                .getTags();
        // Теги у пользователя должны быть настроены
        if (userTags.isEmpty()) {
            throw new SettingsNotDefinedYetException();
        }

        Tag difficultUserTag = tagService.getDifficultTagFromList(userTags);
        Tag languageUserTag = tagService.getLanguageTagFromList(userTags);

        // Получение всех подходящих вопросов
        List<Question> potentialQuestions = questionService.getAllQuestionsContainsBothTags(difficultUserTag, languageUserTag);
        if (potentialQuestions.isEmpty()) {
            // Не нашлось подходящих запросов, измените предпочтения
            throw new NotEvenSinglePotentialQuestionForUserException(
                    "Для технологии " + languageUserTag.getName() +
                            " и сложности " + difficultUserTag.getName() +
                            " не нашлось вопросов для Вас");
        }

        // Выборка рандомного из них
        // TODO: допилить сюда получение вопросов, на которые юзер ещё не отвечал верно
        return potentialQuestions.get(rnd.nextInt(potentialQuestions.size()));

    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {
        String callbackData = update.getCallbackQuery().getData();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        Integer answerIndexFromButton = Integer.valueOf(getPanelAnswerIndexFromCallbackData(callbackData));
        // Меняем старое сообщение чтобы нельзя было на него снова ответить
        EditMessageText editMessageNew = new EditMessageText();
        editMessageNew.setChatId(chatId);
        editMessageNew.setMessageId(messageId);
        editMessageNew.setReplyMarkup(null);

        // TODO: поменять на вызов bot.send(SendMessage message)
        try {
            bot.execute(editMessageNew);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // Пишем сообщение с правильным ответом и комментариями
        Long questionId = Long.parseLong(getPanelQuestionIdFromCallbackData(callbackData));
        Question question = questionService.getQuestionById(questionId);
        List<Answer> answers = question.getAnswers();

        StringBuilder textToSend = new StringBuilder();
        if (answers.get(answerIndexFromButton).getIsTrue()) {
            textToSend.append("Правильно!").append("\n");
        } else {
            Answer correct = answers.stream().filter(ans -> ans.getIsTrue()).findFirst().get();
            textToSend.append("Неправильно!").append("\n");
            textToSend.append("Правильный ответ: ").append(correct.getText()).append("\n");
        }
        textToSend.append("\n").append("Комментарий от автора").append(question.getComment());

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend.toString());
        bot.send(message);

    }

    private String getPanelAnswerIndexFromCallbackData(String callbackData) {
        return callbackData.split("_")[2];
    }

    private String getPanelQuestionIdFromCallbackData(String callbackData) {
        return callbackData.split("_")[3];
    }

}
