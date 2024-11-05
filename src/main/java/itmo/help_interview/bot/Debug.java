package itmo.help_interview.bot;

import itmo.help_interview.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Debug {

    @Autowired
    UserRepository userRepository;

    @EventListener(ApplicationStartedEvent.class)
    public void debug() {
        int i = 1;
    }
}
