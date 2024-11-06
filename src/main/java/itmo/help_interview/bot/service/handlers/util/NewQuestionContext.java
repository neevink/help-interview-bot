package itmo.help_interview.bot.service.handlers.util;

import itmo.help_interview.bot.entity.Question;

public class NewQuestionContext {
    Long chatId;

    NewQuestionContextState state;

    Question.QuestionBuilder questionBuilder;



}
