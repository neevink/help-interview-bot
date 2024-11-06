package itmo.help_interview.bot.service.handlers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import itmo.help_interview.bot.CommonUtil;
import itmo.help_interview.bot.entity.Answer;
import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.repository.AnswerRepository;
import itmo.help_interview.bot.repository.QuestionRepository;
import itmo.help_interview.bot.repository.UserRepository;
import itmo.help_interview.bot.service.CommandHandler;
import itmo.help_interview.bot.service.TelegramBot;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
class AddQuestionCommandHandler implements CommandHandler {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            .build();

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Override
    public void handle(TelegramBot bot, Update update) {
        var jsonLines = CommonUtil.extractText(update).orElseThrow();
//        var questions = Pattern.compile("\n")
//                .splitAsStream(jsonLines)
//                .skip(1)  // command
//                .map(json -> {
//                    try {
//                        return MAPPER.readValue(json, Line.class);
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .map(line -> {
//                    var now = LocalDateTime.now();
//                    var builder = Question.builder()
//                            .user(userRepository.findById(update.getMessage().getChatId())
//                                    .orElseThrow(() -> new RuntimeException("Not found user for question by chat id")))
//                            .isOpen(false)
//                            .text(line.getQuestion())
//                            .creationDate(now)
//                            .isDeleted(false)
//                            .checked(false)
//                            .comment(null)
//                            .id(0L);
////                    var allAnswers = line.getAnswers();
//                    builder.answers(line.makeAnswersFromLineAnswer());
//
//                    return builder.build();
//                }).toList();
//        questionRepository.saveAll(questions);
    }

    @Override
    public String getCommandName() {
        return "/add_question";
    }

//    @Data
//    @Builder
//    @NoArgsConstructor(force = true)
//    @RequiredArgsConstructor
//    private static class Line {
//        private final String question;
//        private final List<Answer> answers;
//        private final List<String> tags;
//
//        record Answer(String text, boolean isRight) {
//        }
//
//        public List<itmo.help_interview.bot.entity.Answer> makeAnswersFromLineAnswer() {
//            return answers.stream()
//                    .map(answer -> new itmo.help_interview.bot.entity.Answer(
//                            0, answer.text, answer.isRight, null
//                    ))
//                    .toList();
//        }
//    }
}


