package itmo.help_interview.bot;

import java.util.Optional;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommonUtil {
    private static final Pattern commandRegex = Pattern.compile("^(/\\w+)\n?.*");

    public static boolean startsWithCommand(String text) {
        return commandRegex.matcher(text).matches();
    }

    public static Optional<String> extractText(Update update) {
        return Optional.ofNullable(update)
                .map(Update::getMessage)
                .map(Message::getText);
    }

    public static Optional<String> extractCommand(String text) {
        var matcher = commandRegex.matcher(text);
        return matcher.find()
                ? Optional.of(matcher.group(1))
                : Optional.empty();
    }
}
