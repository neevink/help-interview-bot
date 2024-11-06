package itmo.help_interview.bot.service;


import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.repository.TagRepository;
import itmo.help_interview.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public void addTagToUser(Long userId, String tagName) {
        // Находим пользователя по его ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Ищем тег по имени или создаем новый
            Tag tag = findOrCreateTagByName(tagName);

            // Добавляем тег пользователю
            user.addTag(tag);

            // Сохраняем изменения
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private Tag findOrCreateTagByName(String tagName) {
        // Ищем тег по имени
        Optional<Tag> tagOptional = tagRepository.findByName(tagName);
        if (tagOptional.isPresent()) {
            return tagOptional.get();
        } else {
            throw new RuntimeException("Tag not found");
        }
    }
}
