# help-interview-bot
## Описание

Проект представляет из себя чат-бота, который будет помогать готовиться к интервью и проверять свои технические навыки в IT-сфере с помощью вопросов в формате квиза. Пользователь может выбрать тематику вопросов и получать их либо по требованию от бота, либо по периодической рассылке. Каждый пользователь имеет свой рейтинг и может добавлять новые вопросы в общую базу, которые сначала проходят стадию модерации от других пользователей (у которых рейтинг больше некоторого значения). Такой децентрализованный подход позволяет системе автономно развиваться.

## Функциональности

1. Пользователь может настраивать желаемый уровень сложности вопросов и темы, по которым он хочет заниматься, получать вопросы по команде либо при помощи автоматической рассылки. Вопросы могут быть с открытым ответом либо со списком ответов. Пользователь может поставить лайк или дизлайк под вопросом, либо пожаловаться на него, если например вместо вопроса написан скам. Если на вопрос поступило достаточно жалоб, его автор банится, а вопрос удаляется. Если вопрос собрал достаточно много дизлайков, он удаляется, но его автор не банится.
2. Пользователь может добавлять новые вопросы в базу данных бота и удалять какие-либо вопросы, которые он ранее создал. Новый вопрос помечается как ещё не прошедший модерацию, то есть он не будет показываться пользователям без статуса модератора. Когда новый вопрос пометят как корректный достаточное количество модераторов, его будут видеть остальные пользователи.
3. Пользователи обладают рейтингом, который увеличивается, когда пользователь правильно отвечает на вопросы, и уменьшается, когда он отвечает неправильно. Если у пользователя достаточно большой рейтинг, ему присваивается статус модератора.

## Что сделано
+ Получение вопросов по тегам темы и сложности;
+ Добавление и удаление вопросов;
+ Модерация и бан;


## Что не сделано

+ Обновление рейтинга пользователя;
+ Нет деплоя (бот живёт локально на ЭВМ разработчиков);

## Команда

* Иванов Евгений (тимлид, реализация телеграмм бота)
* Борисова Анастасия (backend разработчик)
* Неевин Кирилл (архитектор, backend разработчик)
* Калмыков Андрей (аналитик, БД)
* Момонт Игорь (backend разработчик)

## Ссылки

* [Презентация](https://docs.google.com/presentation/d/1s8QZUboEWE_HQVPXRMSIPCbcF761qa8HCZ7h2mkDyz4/edit?usp=sharing)
* [Видео 1](https://drive.google.com/file/d/1zxlTf25ncPBKWTzggd02GxBja5NIHL7E/view?usp=sharing), [Видео 2](https://drive.google.com/file/d/1QUAl8O48UyPiaJuRhYJ9RV_ZiCIMQSBe/view?usp=sharing)
