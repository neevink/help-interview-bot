package itmo.help_interview.bot.service.handlers;

import itmo.help_interview.bot.entity.Answer;
import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.exceptions.NotEvenSinglePotentialQuestionForUserException;
import itmo.help_interview.bot.exceptions.SettingsNotDefinedYetException;
import itmo.help_interview.bot.exceptions.UserNotFoundException;
import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.QuestionService;
import itmo.help_interview.bot.service.TagService;
import itmo.help_interview.bot.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
			textToSend.append(i).append(". ").append(current.getText()).append("\n");
		}

		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

		for (int i = 0; i < answers.size(); i++) {
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(String.valueOf(i + 1)); // Текст кнопки (например, "1", "2" и т.д.)
			button.setCallbackData("answer_" + i); // Данные обратного вызова

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


		// TODO: допилить сюда InlineKeyboard в ответы по количеству кнопок

	}

	@Override
	public void handleCallback(TelegramBot bot, Update update) {

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
}
