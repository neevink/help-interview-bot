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
import itmo.help_interview.bot.service.UserQuestionAnswerService;
import itmo.help_interview.bot.service.UserService;
import itmo.help_interview.bot.service.handlers.util.RatingBanConstantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
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

    private final TagRepository tagRepository;
    private final UserQuestionAnswerRepository userQuestionAnswerRepository;

    private final TagService tagService;
    private final UserService userService;
    private final QuestionService questionService;
    private final RatingBanConstantsService ratingBanConstantsService;
    private final UserQuestionAnswerService userQuestionAnswerService;

    private static Random rnd = new Random(42);

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
        // Клавиатура с ответами
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
        userTags = userService.getUserById(chatId).getTags();
        // Теги у пользователя должны быть настроены
        if (userTags.isEmpty()) {
            throw new SettingsNotDefinedYetException();
        }

        Tag difficultUserTag = tagService.getDifficultTagFromList(userTags);
        Tag languageUserTag = tagService.getLanguageTagFromList(userTags);

        // Получение всех подходящих вопросов
        List<Question> potentialQuestions = questionService.getAllQuestionsContainsBothTags(difficultUserTag, languageUserTag);
        // Отсеиваем все удалённые вопросы
        potentialQuestions = potentialQuestions.stream()
                .filter(question -> !question.getIsDeleted())
                .toList();
        // Получение вопросов, на которые юзер ещё не отвечал ВООБЩЕ
        // TODO подумать про то, можно ли возвращать вопросы, на которые юзер ответил неверно когда-то...
        Set<Long> viewedQuestionIds = userQuestionAnswerRepository.findAllByUser_chatId(chatId).stream()
                .map(UserQuestionAnswer::getQuestionId)
                .collect(Collectors.toSet());
        potentialQuestions = potentialQuestions.stream()
                .filter(question -> !viewedQuestionIds.contains(question.getId()))
                .toList();

        // Если чел модер, то может получать новые вопросы (но надо передать флаг и делать новые кнопки)
        if (ratingBanConstantsService.userAllowToModerateQuestions(chatId)) {
            // Случай что могут прийти вопросы со стадии модерации, итс окэй и проверяется и обрабатывается дальше
        } else {
            // В таком случае оставляем только checked = true вопросы
            potentialQuestions = potentialQuestions.stream()
                    .filter(Question::getChecked)
                    .toList();
        }

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

        if (getCommandLineTypeOfCallback(callbackData) == 4) {
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

            // ОЦЕНИТЬ ВОПРОС если юзер может быть модером и вопрос ещё на модерации
            InlineKeyboardMarkup markup = null;
            if (!question.getChecked()) {
                // Случай когда вопрос всё ещё проходит модерацию

                // TODO проверять что на данной стадии юзер всё ещё модер и не забанен часом
                if (ratingBanConstantsService.isUserBanned(chatId)) {
                    // Юзер то забанен, в модерации не участвует
                } else {
                    if (ratingBanConstantsService.userAllowToModerateQuestions(chatId)) {
                        textToSend.append("\n\n");
                        textToSend.append("Вы являетесь модератором данного вопроса, пожалуйста, оцените его. " +
                                "В данном случае:\nAPPROVE - вопрос хороший и " +
                                "вписывается в общий банк под свою категорию и сложность;\n" +
                                "BLOCK - вопрос плохо сформулирован/не соотносится с темой и сложностью;\n" +
                                "REPORT - жалоба на вопрос (спам, реклама, призыв к чему-либо);");

                        markup = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                        rowsInline.add(createModerationQuestionRow(questionId));
                        markup.setKeyboard(rowsInline);
                    }
                }
            }

            bot.send(chatId, textToSend.toString(), markup);

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
        } else if (getCommandLineTypeOfCallback(callbackData) == 5) {
            // Окей, модератор нажал кнопку оценки, логика обработки тут
            String pressedFeedback = getFeedbackCommandUserPressedFromCallbackData(callbackData);
            Long questionId = Long.parseLong(getPanelQuestionIdFromCallbackData(callbackData));
            User user = userService.getUserById(chatId);

            // Если он нажал кнопку когда уже не был модератором, то надо об этом сообщить и ничего не делать более
            if (!ratingBanConstantsService.userAllowToModerateQuestions(chatId)) {
                String textThatUserNotModerRightNow = "На момент Вашей оценки, вы уже не являетесь модератором";
                bot.sendEditMessage(chatId, textThatUserNotModerRightNow, messageId);
                return;
            }

            // Если мог модерить, то оставил одну из оценок
            // Теперь выведем информацию об этом прямо в этом же сообщении
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(messageId);
            String thanksForModerWork = "Ваша оценка [" + pressedFeedback
                    + "] была принята. Большое спасибо, что развиваете наше сообщество!";
            editMessageText.setText(thanksForModerWork);

            bot.send(editMessageText);

            // Осталось изменить в базе посещение данного вопроса и оценку в нём на выбранную
            List<UserQuestionAnswer> moderForThisQuestion =
                    userQuestionAnswerRepository.findAllByUser_chatIdAndQuestion_id(chatId, questionId);
            // Посещений данного вопроса в целом может быть много в будущем, но пока оно максимум 1
            UserQuestionAnswer moderAnswer = moderForThisQuestion.get(0);
            switch (pressedFeedback) {
                case "approve":
                    moderAnswer.setReaction(UserQuestionAnswerReaction.APPROVE);
                    break;
                case "block":
                    moderAnswer.setReaction(UserQuestionAnswerReaction.BLOCK);
                    break;
                case "report":
                    moderAnswer.setReaction(UserQuestionAnswerReaction.REPORT);
                    break;
                default:
                    // TODO не должно быть такого, игнорировать тут (подумать на досуге)
                    break;
            }
            userQuestionAnswerService.updateAndLoadQuestionCheck(moderAnswer);
        }
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

    // Возвращаем кол-во слов по split '_', если их 4 -- это варианты ответов, 5 -- это ответ с модерации.
    private int getCommandLineTypeOfCallback(String callbackData) {
        return callbackData.split("_").length;
    }

    private String getFeedbackCommandUserPressedFromCallbackData(String callbackData) {
        return callbackData.split("_")[2];
    }

    private String getPanelAnswerIndexFromCallbackData(String callbackData) {
        return callbackData.split("_")[2];
    }

    private String getPanelQuestionIdFromCallbackData(String callbackData) {
        return callbackData.split("_")[3];
    }

    private List<InlineKeyboardButton> createModerationQuestionRow(long questionId) {
        List<InlineKeyboardButton> moderationRow = new ArrayList<>();

        InlineKeyboardButton approveButton =
                createButtonKeyboard("APPROVE", "/get_question_approve_" + questionId + "_" + questionId);
        moderationRow.add(approveButton);

        InlineKeyboardButton blockButton =
                createButtonKeyboard("BLOCK", "/get_question_block_" + questionId + "_" + questionId);
        moderationRow.add(blockButton);

        InlineKeyboardButton reportButton =
                createButtonKeyboard("REPORT", "/get_question_report_" + questionId + "_" + questionId);
        moderationRow.add(reportButton);

        return moderationRow;
    }

    private InlineKeyboardButton createButtonKeyboard(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text); // Устанавливаем текст кнопки
        inlineKeyboardButton.setCallbackData(callbackData); // Устанавливаем callback data
        return inlineKeyboardButton;
    }

}
