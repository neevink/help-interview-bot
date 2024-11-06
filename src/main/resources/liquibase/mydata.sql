INSERT INTO bot_user (id, is_banned, rating)
VALUES (1273082747, false, 100);

INSERT INTO tag (id, category, name)
VALUES (1, 'DIFFICULTY', 'Junior'),
       (2, 'DIFFICULTY', 'Middle'),
       (3, 'DIFFICULTY', 'Senior'),
       (4, 'LANGUAGE', 'Java'),
       (5, 'LANGUAGE', 'C++'),
       (6, 'LANGUAGE', 'Python');

INSERT INTO user_tags (user_id, tag_id)
VALUES (1273082747, 1),
       (1273082747, 4);

INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (1, true, 'Что такое ООП?', true, NULL, '2010-01-02', false, 1273082747),
       (2, true, 'За что отвечает JVM?', true, NULL, '2020-10-10', false, 1273082747),
       (3, false, 'test question 3', false, 'ТЕСТ КОММЕНТ', now(), false, 1273082747);

INSERT INTO question_tags (question_id, tag_id)
VALUES (1, 1),
       (1, 4),
       (1, 5),
       (2, 1),
       (3, 6),
       (3, 2),
       (2, 2);

INSERT INTO answer (id, question_id, text, is_true)
VALUES (1, 1, 'int', true),
       (2, 2, 'float', true),
       (3, 3, 'PY 1', false),
       (4, 3, 'PY 2', true);

insert into user_answers
values (1, 1273082747, 1, true, 'APPROVE'),
       (2, 1273082747, 2, true, 'REPORT'),
       (3, 1273082747, 3, false, 'BLOCK');