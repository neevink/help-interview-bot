package itmo.help_interview.bot.service.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.TagCategory;
import itmo.help_interview.bot.repository.TagRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TagService;
import itmo.help_interview.bot.service.TelegramBot;
import itmo.help_interview.bot.service.UserService;
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
class AddQuestionCommandHandler implements CommandHandler {

    private final TagRepository tagRepository;
    private final UserService userService;

    // TODO удалять контексты после всего процесса
    private final Map<Long, NewQuestionContext> userTONewQuestionContext = new HashMap<>();
    private final TagService tagService;

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
            button.setCallbackData("/add_question_technology" + tag.getName()); // Используем текст тега в качестве callback data
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
                        "\n\nТеперь введите *текст вопроса* одним сообщением ниже");

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


}


