package itmo.help_interview.bot.service.handlers;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import itmo.help_interview.bot.CommonUtil;
import itmo.help_interview.bot.entity.Question;
import itmo.help_interview.bot.repository.QuestionRepository;
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

    @Override
    public void handle(TelegramBot bot, Update update) {
        var jsonLines = CommonUtil.extractText(update).orElseThrow();
        var questions = Pattern.compile("\n")
                .splitAsStream(jsonLines)
                .skip(1)  // command
                .map(json -> {
                    try {
                        return MAPPER.readValue(json, Line.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(line -> {
                    var now = Instant.now();
                    var builder = Question.builder()
                            .userId(update.getMessage().getChatId())
                            .isOpen(false)
                            .text(line.getQuestion())
                            .createdAt(now)
                            .isDeleted(false)
                            .checked(false)
                            .comment(null)
                            // хак чтобы не мучаться с генерацией айдишников
                            .id(ThreadLocalRandom.current().nextLong(10, 1L << 16));
                    var answers = line.getAnswers().stream()
                            .collect(Collectors.partitioningBy(Line.Answer::isRight));
                    builder.rightAnswer(answers.get(true).get(0).text());
                    var wrongAnswers = answers.get(false);
                    builder.bAnswer(wrongAnswers.get(0).text());
                    builder.cAnswer(wrongAnswers.get(1).text());
                    builder.dAnswer(wrongAnswers.get(2).text());
                    return builder.build();
                }).toList();
        questionRepository.saveAll(questions);
    }

    @Override
    public String getCommandName() {
        return "/add_question";
    }

    @Data
    @Builder
    @NoArgsConstructor(force = true)
    @RequiredArgsConstructor
    private static class Line {
        private final String question;
        private final List<Answer> answers;
        private final List<String> tags;

        record Answer(String text, boolean isRight) {
            //
        }
    }
}


