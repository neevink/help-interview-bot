package itmo.help_interview.bot.service;


import itmo.help_interview.bot.entity.Tag;
import itmo.help_interview.bot.entity.User;
import itmo.help_interview.bot.exceptions.UserNotFoundException;
import itmo.help_interview.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagService tagService;

    @Transactional
    public void addTagToUser(Long userId, String tagName) {
        // Находим пользователя по его ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Ищем тег по имени или создаем новый
            Tag tag = tagService.findTagByNameOrThrow(tagName);

            // Добавляем тег пользователю
            user.addTag(tag);

            // Сохраняем изменения
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public User getUserById(long chatId) {
        return userRepository.findById(chatId).orElseThrow(
				UserNotFoundException::new
        );
    }

    public void clearUserTagsByUserId(long chatId) {
        User user = getUserById(chatId);
        user.setTags(new ArrayList<>());
        userRepository.save(user);
    }

}
