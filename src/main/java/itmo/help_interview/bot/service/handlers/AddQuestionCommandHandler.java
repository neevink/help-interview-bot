package itmo.help_interview.bot.service.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itmo.help_interview.bot.entity.Answer;
import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.TagCategory;
import itmo.help_interview.bot.repository.TagRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.QuestionService;
import itmo.help_interview.bot.service.TagService;
import itmo.help_interview.bot.service.TelegramBot;
import itmo.help_interview.bot.service.handlers.util.NewQuestionContext;
import itmo.help_interview.bot.service.handlers.util.NewQuestionContextState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@RequiredArgsConstructor
public class AddQuestionCommandHandler implements CommandHandler {

    private final TagRepository tagRepository;

    // TODO удалять контексты после всего процесса
    private final Map<Long, NewQuestionContext> userTONewQuestionContext = new HashMap<>();
    private final TagService tagService;
    private final QuestionService questionService;

    @Override
    public String getCommandName() {
        return "/add_question";
    }


    @Override
    public void handle(TelegramBot bot, Update update) {
        long chatId = update.getMessage().getChatId();
        // TODO Отправлять отдельным первым сообщением инструкцию по созданию своего вопроса


        // Создаём новый инстанс контекста для данного пользователя и переводим его в WAITING_FOR_TAGS
        // TODO не забыть проверять на наличие контекста, если у пользователя уже есть контекст, тогда выводить ему
        // TODO ошибку и не менять состояние (просто пришедшую команду /add_question игнорировать (с ответным сообщением)
        if (userTONewQuestionContext.containsKey(chatId)) {
            // TODO см. выше
        }
        NewQuestionContext context = new NewQuestionContext();
        context.setChatId(chatId);
        context.setQuestion(new Question());
        context.setState(NewQuestionContextState.WAITING_FOR_TAGS);
        userTONewQuestionContext.put(chatId, context);

        // Отправляем сообщение с началом процесса -- выбором тегов (сложности и технологии)
        String answer = "Выберете технологию для вопроса";
        // Получаем список тегов из репозитория с фильтром по технологиям (языкам)
        List<Tag> tags = tagRepository.findAll().stream()
                .filter(tag -> TagCategory.LANGUAGE.equals(tag.getCategory()))
                .toList();

        // Создаем InlineKeyboardMarkup
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Tag tag : tags) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(tag.getName());
            button.setCallbackData("/add_question_technology_" + tag.getName()); // Используем текст тега в качестве callback data
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        // Отправляем сообщение с клавиатурой
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(answer);
        message.setReplyMarkup(inlineKeyboardMarkup);

        bot.send(message);
    }

    @Override
    public void handleCallback(TelegramBot bot, Update update) {
        String callbackData = update.getCallbackQuery().getData();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        // TODO если каким то образом контекст удалился за время колбэка то выдавать игнор (или ошибку что процесс уже завершен)
        if (!userTONewQuestionContext.containsKey(chatId)) {
            //
        }
        NewQuestionContext currentUserContext = userTONewQuestionContext.get(chatId);
        Question question = currentUserContext.getQuestion();

        switch (getPanelTagTypeFromCallbackData(callbackData)){
            case "technology":
                String technologyTagPressed = getPressedButtonFromCallbackData(callbackData);
                // Добавим тег технологии к будущему вопросу
                if (question.getTags() == null) {
                    question.setTags(new ArrayList<>());
                }
                // доп проверка что не запишем больше 2 тегов в сумме
                if (!question.getTags().isEmpty()) {
                    question.getTags().clear();
                }
                tagService.addTagForQuestionByTagName(question, technologyTagPressed);

                // Теперь выбор тега сложности
                String newText = "Вы выбрали тег: " + getPressedButtonFromCallbackData(callbackData) +
                        "\nВыберите сложность вашего вопроса";
                // Отправляем пользователю клавиатуру с сложностями
                InlineKeyboardMarkup newKeyboard = createDifficultyKeyboard();
                // Создание EditMessageText для замены текста и клавиатуры
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(messageId);
                editMessage.setText(newText);
                editMessage.setReplyMarkup(newKeyboard);

                bot.send(editMessage);
                break;

            case "difficulty":
                String difficultyTagPressed = getPressedButtonFromCallbackData(callbackData);
                // Добавим тег сложности к будущему вопросу
                if (question.getTags() == null) {
                    // TODO ошибка если оказалось так что нет тега технологии пробрасывать и начинать заново выборку
                }
                // доп проверка что не запишем больше 2 тегов в сумме, на данном этапе должен быть 1 тег технологии и только
                if (!(question.getTags().size() == 1 &&
                        TagCategory.LANGUAGE.equals(question.getTags().get(0).getCategory()))) {
                    // TODO Если оказалось не так что тег ровно один и это тег технологий то ошибка
                    throw new RuntimeException();
                }
                tagService.addTagForQuestionByTagName(question, difficultyTagPressed);

                // Меняем состояние у контекста на ожидание текста вопроса
                currentUserContext.setState(NewQuestionContextState.WAITING_FOR_QUESTION_TEXT);

                // Пользователю в сообщение с выборкой тегов пишем его выбор и в новом сообщении пишем что надо ввести
                // текст вопроса
                EditMessageText editMessageNew = new EditMessageText();
                editMessageNew.setChatId(chatId);
                editMessageNew.setMessageId(messageId);
                editMessageNew.setText("Вы выбрали технологию: " + question.getTags().get(0).getName() +
                        "\nВы выбрали уровень вопроса: " + difficultyTagPressed +
                        "\n\nТеперь введите текст вопроса одним сообщением ниже");

                bot.send(editMessageNew);
                break;
            default:
                break;
        }
    }


    private String getPanelTagTypeFromCallbackData(String callbackData){
        return callbackData.split("_")[2];
    }

    private String getPressedButtonFromCallbackData(String callbackData){
        return callbackData.split("_")[3];
    }


    // Создаёт клавиатуру с тегами сложности
    private InlineKeyboardMarkup createDifficultyKeyboard() {
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
            button.setCallbackData("/add_question_difficulty_" + tag.getName()); // Используем текст тега в качестве callback data
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    /**
     * По абстракции этот метод и следующие можно либо вынести в CommandHandler
     * Либо продумать как бы его можно было реализовать через проверку всевозможных контекстов (пока он один)
     * В каком-то нужном порядке. А ещё данный метод приходится вызывать из бота при сообщении напрямую к инстансу
     * этого хендлера, что является плохим подходом. Мне видится реализация такого метода через новый интерфейс,
     * который говорил бы, что данный CommandHandler имеет в себе какой-то контекст.
     *
     * ПОКА ЧТО НЕ ВАЖНО, КОНТЕКСТ ОДИН И СРОКИ ПОДЖИМАЮТ
     */
    // Метод проверяет, ожидает ли сейчас сообщение от пользователя для этого контекста
    public boolean checkNewMessageForContextNeeded(Update update) {
        long chatId = update.getMessage().getChatId();
        // Если вообще нет контекста, то юзер просто написал чё-то без процесса создания нового вопроса
        if (!userTONewQuestionContext.containsKey(chatId)) {
            return false;
        }
        NewQuestionContextState currentState = userTONewQuestionContext.get(chatId).getState();
        // Проверка состояния контекста
        boolean stateNeedTextMessage = false;
        switch (currentState) {
            case WAITING_FOR_TAGS, WAITING_FOR_APPROVE_OR_EDITING -> stateNeedTextMessage = false;
            case WAITING_FOR_QUESTION_TEXT, WAITING_FOR_ANSWERS_AND_COMMENT -> stateNeedTextMessage = true;
        }
        return stateNeedTextMessage;
    }

    // Метод получает входящее сообщение и адресует его на данный контекст для данного пользователя
    public void manageContextWithNewMessageFromUser(TelegramBot bot, Update update) {
        long chatId = update.getMessage().getChatId();
        if (!userTONewQuestionContext.containsKey(chatId)) {
            // TODO Возврат ошибки о том, что случайно оказалось, что контекст не найден и для создания вопроса
            // TODO необходимо снова выполнить /add_question
            return;
        }

        NewQuestionContext context = userTONewQuestionContext.get(chatId);
        // Передаём обработку в нужное русло (зависит от данного состояния)
        switch (context.getState()) {
            case WAITING_FOR_QUESTION_TEXT:
                contextGetQuestionText(bot, update, context);
                break;
            case WAITING_FOR_ANSWERS_AND_COMMENT:
                contextGetAnswersAndCommentsText(bot, update, context);
                break;
            default:
                // Случай когда мы зашли в этот метод но при этом не ждём сообщения, что странно
                // TODO пробрасывать ошибку, пока можно просто return (никогда не должны сюда заходить по логике)
                return;
        }
    }

    // Метод для заполнения текста будущего вопроса (или редактирования этого поля) пришедшим сообщением
    private void contextGetQuestionText(TelegramBot bot, Update update, NewQuestionContext context) {
        // Заполняем будущий question.text
        String textOf = update.getMessage().getText();
        context.getQuestion().setText(textOf);

        // Переходим к ожиданию ответов на вопрос и комментарию
        context.setState(NewQuestionContextState.WAITING_FOR_ANSWERS_AND_COMMENT);

        // Выводим пользователю НОВОЕ сообщение с просьбой указать ответы и комментарий
        StringBuilder textToAnswer = new StringBuilder();
        textToAnswer.append("Вы ввели вопрос:\n\n")
                .append(textOf)
                .append("\n\nВведите одним сообщением ниже все ответы и комментарий от автора к вопросу ")
                .append("в следующем формате \n<ПРАВИЛЬНЫЙ_ОТВЕТ>\n<КОММЕНТАРИЙ_ОТ_АВТОРА>")
                .append("\n<НЕПРАВИЛЬНЫЙ_ОТВЕТ>\n<НЕПРАВИЛЬНЫЙ_ОТВЕТ>\n...")
                .append("\nУчтите, что между ответами и комментариями один перевод строки ")
                .append("(не используйте перевод строки в любом из ответов). ")
                .append("Комментарий не может быть пустым. ")
                .append("Может быть 1-5 неправильных ответов.");

        // TODO Добавить в ответ клавиатуру с отменой всего процесса или переходом к шагу редактирования тегов/текста

        SendMessage message = new SendMessage();
        message.setChatId(context.getChatId());
        message.setText(textToAnswer.toString());

        bot.send(message);
    }

    // Метод парсит полученное сообщение в правильный ответ, комментарий от автора, неправильные ответы
    private void contextGetAnswersAndCommentsText(TelegramBot bot, Update update, NewQuestionContext context) {
        String[] answersOrComments = update.getMessage().getText().split("\n");
        if (answersOrComments.length < 3 || answersOrComments.length > 7) {
            // Неправильный парсер (ответов либо слишком мало, либо слишком много)
            StringBuilder textToAnswer = new StringBuilder();
            textToAnswer.append("Вы ввели некорректные ответы и(-или) комментарий")
                    .append("\n\nВведите ответы на вопрос и комментарий заново");

            // TODO Добавить в ответ клавиатуру с отменой всего процесса или переходом к шагу редактирования тегов/текста
            SendMessage message = new SendMessage();
            message.setChatId(context.getChatId());
            message.setText(textToAnswer.toString());

            bot.send(message);
            return;
        }

        // Обновляем ответы и комментарий
        Question question = context.getQuestion();
        question.setComment(answersOrComments[1]);

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(0, answersOrComments[0], true, null));
        for (int i = 2; i < answersOrComments.length; i++) {
            // Wrong answers
            answers.add(new Answer(null, answersOrComments[i], false, null));
        }
        question.setAnswers(answers);

        // Обновляем состояние контекста
        context.setState(NewQuestionContextState.WAITING_FOR_APPROVE_OR_EDITING);

        // Отправляем пользователю НОВОЕ сообщение с просьбой подтвердить или редактировать весь вопрос (всё на кнопках)
        // Так что нужен callback обработчик
        SendMessage message = new SendMessage();
        message.setChatId(context.getChatId());
        message.setText("Последний шаг! Вы можете либо сохранить вопрос (он отправится на модерацию), " +
                "либо редактировать/отменить его");
        // TODO добавить клавиатуру для редактирования или отмены
        // TODO в таком случае через callback отлавливать savequestion и сохранять через метод в QuestionService

        bot.send(message);

        // СОХРАНЕНИЕ ПОКА ЧТО ПРОИСХОДИТ СРАЗУ ЖЕ
        // TODO убрать отсюда (по идее надо эти вызовы делать из callback вызова по savenewquestion по данному контексту
        Question createdQuestion = questionService.saveNewQuestionByNewQuestionContext(context);
        // Тут же сообщение о том что за вопрос сохранился
        SendMessage messageWithCreatedQuestion = new SendMessage();
        messageWithCreatedQuestion.setChatId(context.getChatId());
        StringBuilder sb = new StringBuilder();
        sb.append("Вы сохранили вопрос: ").append(createdQuestion.getText());
        sb.append("\n\nВарианты ответов:");
        question.getAnswers().stream().forEach(ans -> {
            sb.append("\n").append(ans.getText());
            if (ans.getIsTrue()) {
                sb.append(" [true]");
            }
        });
        sb.append("\n\nКомментарий от автора: ").append(question.getComment());
        messageWithCreatedQuestion.setText(sb.toString());

        bot.send(messageWithCreatedQuestion);
    }
}


