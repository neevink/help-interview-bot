package itmo.help_interview.bot.service.handlers.util;

public enum NewQuestionContextState {
    WAITING_FOR_TAGS,
    WAITING_FOR_QUESTION_TEXT,
    WAITING_FOR_ANSWERS_AND_COMMENT,
    WAITING_FOR_APPROVE_OR_EDITING
}
