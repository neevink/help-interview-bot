package itmo.help_interview.bot.service.handlers;

import itmo.help_interview.bot.entity.Answer;
import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.entity.UserQuestionAnswer;
import itmo.help_interview.bot.entity.UserQuestionAnswerReaction;
import itmo.help_interview.bot.exceptions.NotEvenSinglePotentialQuestionForUserException;
import itmo.help_interview.bot.exceptions.SettingsNotDefinedYetException;
import itmo.help_interview.bot.exceptions.UserNotFoundException;
import itmo.help_interview.bot.repository.TagRepository;
import itmo.help_interview.bot.repository.UserQuestionAnswerRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.QuestionService;
import itmo.help_interview.bot.service.TagService;
import itmo.help_interview.bot.service.TelegramBot;
import itmo.help_interview.bot.service.UserService;
import itmo.help_interview.bot.service.handlers.util.RatingBanConstantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Обработка команды получения вопроса /get_question
 */
@Component
@RequiredArgsConstructor
public class GetQuestionCommandHandler implements CommandHandler {


    private final TagService tagService;
    private final UserService userService;
    private final QuestionService questionService;
    private final RatingBanConstantsService ratingBanConstantsService;

    private static Random rnd = new Random(42);
    private final UserQuestionAnswerRepository userQuestionAnswerRepository;
    private final TagRepository tagRepository;

    @Override
    public void handle(TelegramBot bot, Update update) {
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
        Long questionId = questionForAsk.getId();
        List<Answer> answers = questionForAsk.getAnswers();
        String textToSend = generateQuestionFullTextFromQuestionId(questionId);
        // Клавиатура с овтетами
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i + 1)); // Текст кнопки (например, "1", "2" и т.д.)
            // Данные обратного вызова
            button.setCallbackData("/get_question_" + i + "_" + questionId);
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        markup.setKeyboard(rowsInline);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
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
        userTags = userService.getUserById(chatId).getTags();
        // Теги у пользователя должны быть настроены
        if (userTags.isEmpty()) {
            throw new SettingsNotDefinedYetException();
        }

        Tag difficultUserTag = tagService.getDifficultTagFromList(userTags);
        Tag languageUserTag = tagService.getLanguageTagFromList(userTags);

        // Получение всех подходящих вопросов
        List<Question> potentialQuestions = questionService.getAllQuestionsContainsBothTags(difficultUserTag, languageUserTag);
        // Получение вопросов, на которые юзер ещё не отвечал ВООБЩЕ
        // TODO подумать про то, можно ли возвращать вопросы, на которые юзер ответил неверно когда-то...
        Set<Long> viewedQuestionIds = userQuestionAnswerRepository.findAllByUser_chatId(chatId).stream()
                .map(UserQuestionAnswer::getQuestionId)
                .collect(Collectors.toSet());
        potentialQuestions = potentialQuestions.stream()
                .filter(question -> !viewedQuestionIds.contains(question.getId()))
                .toList();

        if (potentialQuestions.isEmpty()) {
            // Не нашлось подходящих запросов, измените предпочтения
            throw new NotEvenSinglePotentialQuestionForUserException(
                    "Для технологии " + languageUserTag.getName() +
                            " и сложности " + difficultUserTag.getName() +
                            " не нашлось (новых) вопросов для Вас");
        }
        // Выборка рандомного из них
        return potentialQuestions.get(rnd.nextInt(potentialQuestions.size()));
    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {
        String callbackData = update.getCallbackQuery().getData();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        Integer answerIndexFromButton = Integer.valueOf(getPanelAnswerIndexFromCallbackData(callbackData));
        Long questionId = Long.parseLong(getPanelQuestionIdFromCallbackData(callbackData));
        // Меняем старое сообщение чтобы нельзя было на него снова ответить
        bot.sendEditMessage(chatId, generateQuestionFullTextFromQuestionId(questionId), messageId);

        // Проверяем сообщение и пишем сообщение с правильным ответом и комментариями
        Question question = questionService.getQuestionById(questionId);
        boolean isAnswerCorrect;
        List<Answer> answers = question.getAnswers();
        StringBuilder textToSend = new StringBuilder();

        if (answers.get(answerIndexFromButton).getIsTrue()) {
            // Правильный ответ, помечаем, что пользователь ответил правильно
            isAnswerCorrect = true;
            textToSend.append("Правильно!").append("\n");
        } else {
            // Неправильный ответ
            isAnswerCorrect = false;
            Answer correct = answers.stream().filter(Answer::getIsTrue).findFirst().get();
            textToSend.append("Неправильно!").append("\n");
            textToSend.append("Правильный ответ: ").append(correct.getText()).append("\n");
        }
        textToSend.append("\n");
        String comment = question.getComment();
        if (comment == null || comment.isBlank()) {
            textToSend.append("Автор не оставил комментария");
        } else {
            textToSend.append("Комментарий от автора:\n").append(comment);
        }

        bot.send(chatId, textToSend.toString());

        // Переходим к логике выдаче или снятия рейтинга за ответ
        Tag questionDifficulty = tagService.getDifficultTagFromList(question.getTags());
        ratingBanConstantsService.computeNewUserRatingAfterHisAnswer(chatId, isAnswerCorrect, questionDifficulty);
        // Заполняем связь о том, что пользователь просматривал данный вопрос и дал на него некоторый ответ
        User user = userService.getUserById(chatId);
        userQuestionAnswerRepository.save(new UserQuestionAnswer(
                null,
                user,
                question,
                isAnswerCorrect,
                null
        ));
    }

	private String generateQuestionFullTextFromQuestionId(Long questionId) {
        // Логика подачи вопроса пользователю
        StringBuilder textToSend = new StringBuilder();
        Question question = questionService.getQuestionById(questionId);
        textToSend.append("Вопрос: ").append(question.getText()).append("\n\nОтветы:\n");
        List<Answer> answers = question.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            Answer current = answers.get(i);
            textToSend.append(i + 1).append(". ").append(current.getText()).append("\n");
        }

        return textToSend.toString();
	}

    private String getPanelAnswerIndexFromCallbackData(String callbackData) {
        return callbackData.split("_")[2];
    }

    private String getPanelQuestionIdFromCallbackData(String callbackData) {
        return callbackData.split("_")[3];
    }

}
