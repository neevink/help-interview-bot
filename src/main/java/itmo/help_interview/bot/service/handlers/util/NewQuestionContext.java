package itmo.help_interview.bot.service.handlers.util;

import itmo.help_interview.bot.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewQuestionContext {
    private Long chatId;
    private NewQuestionContextState state;
    private Question question;
    private NewQuestionContextState beforeEditing;
}
