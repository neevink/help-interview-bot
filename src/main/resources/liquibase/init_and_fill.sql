--Создаёт БД и заполняет её данными

DO $$ 
DECLARE
eugen_id integer := 1273082747;
nastya_id integer := 1051467318;
igor_id integer := 1481223955;
kirill_id integer := 520249743;
andrew_id integer := 810518543;
all_author_id integer := eugen_id;
BEGIN

-- Создание основных таблиц
CREATE TYPE tag_category AS ENUM (
    'DIFFICULTY',
    'LANGUAGE');

CREATE TABLE if not exists question
(
    id            BIGSERIAL PRIMARY KEY,
    checked       BOOLEAN DEFAULT false NOT NULL,
    text          TEXT                  NOT NULL,
    is_open       BOOLEAN DEFAULT false NOT NULL,
    comment       TEXT,
    creation_date TIMESTAMP             NOT NULL,
    is_deleted    BOOLEAN DEFAULT false NOT NULL,
    user_id       INTEGER               NOT NULL
);

CREATE TABLE if not exists answer
(
    id          SERIAL PRIMARY KEY,
    question_id INTEGER               NOT NULL REFERENCES question (id) ON DELETE CASCADE,
    text        TEXT                  NOT NULL,
    is_true     BOOLEAN DEFAULT false NOT NULL
);


CREATE TABLE if not exists question_tags
(
    question_id integer NOT NULL,
    tag_id      integer NOT NULL
);

CREATE TABLE if not exists tag
(
    id       SERIAL PRIMARY KEY,
    category tag_category NOT NULL,
    name     TEXT         NOT NULL
);

CREATE SEQUENCE if not exists tag_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

ALTER SEQUENCE tag_id_seq OWNED BY tag.id;

CREATE TABLE if not exists bot_user
(
    id        bigint PRIMARY KEY,
    is_banned BOOLEAN DEFAULT false NOT NULL,
    rating    INTEGER DEFAULT 0     NOT NULL
);

CREATE TYPE user_answer_reaction AS ENUM (
    'APPROVE',
    'BLOCK',
    'REPORT');

CREATE TABLE if not exists user_answers
(
    id          bigserial primary key,
    user_id     bigint                NOT NULL,
    question_id bigint                NOT NULL,
    is_true     BOOLEAN DEFAULT false NOT NULL,
    reaction    user_answer_reaction
);

CREATE TABLE if not exists user_tags
(
    user_id INTEGER NOT NULL,
    tag_id  INTEGER NOT NULL
);


--заполнение основных таблиц

INSERT INTO bot_user (id, is_banned, rating)
VALUES (all_author_id, false, 100);

INSERT INTO tag (id, category, name)
VALUES (1, 'DIFFICULTY', 'Junior'),
       (2, 'DIFFICULTY', 'Middle'),
       (3, 'DIFFICULTY', 'Senior'),
       (4, 'LANGUAGE', 'Java'),
       (5, 'LANGUAGE', 'C++'),
       (6, 'LANGUAGE', 'Python');

INSERT INTO user_tags (user_id, tag_id)
VALUES (all_author_id, 1),
       (all_author_id, 4);

INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (1, true, 'Что такое ООП?', true, NULL, '2010-01-02', false, all_author_id),
       (2, true, 'За что отвечает JVM?', true, NULL, '2020-10-10', false, all_author_id),
       (3, false, 'test question 3', false, 'ТЕСТ КОММЕНТ', now(), false, all_author_id);

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
values (1, all_author_id, 1, true, 'APPROVE'),
       (2, all_author_id, 2, true, 'REPORT'),
       (3, all_author_id, 3, false, 'BLOCK');


--заполение таблицы вопросов


--4 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (4, true, 'Выберите верные утверждения о языке программирования Python', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(5, 4, 'высокоуровневый', true),
	(6, 4, 'низкоуровневый', false),
	(7, 4, 'интерпретируемый', true),
	(8, 4, 'компилируемый', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (4, 1),
       (4, 6);



--5 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (5, true, 'Обязательно ли в Пайтоне объявлять тип переменной?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(9, 5, 'Да', false),
	(10, 5, 'Нет', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (5, 1),
       (5, 6);





--6 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (6, true, 'Какие из представленных фрагментов кода являются литералами?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(11, 6, '''#''', true),
	(12, 6, '0.77', true),
        (13, 6, '5**2', false),
        (14, 6, '[1, 2]', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (6, 1),
       (6, 6);




--7 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (7, true, 'Какие из предлагаемых имен допустимы в Python?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(15, 7, 's_5', true),
	(16, 7, '_5s', false),
        (17, 7, '5_s', false),
        (18, 7, 'Щ5', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (7, 1),
       (7, 6);




--8 Python Java C++ Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (8, true, 'Как будет выглядеть имя переменной в верблюжьей нотации при использовании фразы «show must go on»?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(19, 8, 'ShowMustGoOn', false),
	(20, 8, 'show_must_go_on', false),
        (21, 8, 'showMustGoOn', true),
        (22, 8, 'SHOW_MUST_GO_ON', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (8, 1),
       (8, 6),
       (8, 4),
       (8, 5);



--9 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (9, true, 'Что по умолчанию служит концом инструкции в Python? ', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(23, 9, 'конец строки', true),
	(24, 9, 'запятая', false),
        (25, 9, 'точка', false),
        (26, 9, 'точка с запятой', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (9, 1),
       (9, 6);




--10 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (10, true, 'Для выделения блоков кода одного уровня вложенности в Питоне используются...', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(27, 10, 'круглые скобки', false),
	(28, 10, 'квадратные скобки', false),
        (29, 10, 'фигурные скобки', false),
        (30, 10, 'идентичные отступы', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (10, 1),
       (10, 6);






--11 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (11, true, 'Какие из представленных символов или их комбинаций используются в Python для комментирования кода?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(31, 11, '#', true),
	(32, 11, '//', false),
        (33, 11, '<!-- -->', false),
        (34, 11, '/* */', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (11, 1),
       (11, 6);




--12 Python Java C++ Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (12, true, 'Чем отличаются операторы = и ==?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(35, 12, 'операторы эквивалентны', false),
	(36, 12, 'оператор = менее точный', false),
        (37, 12, 'оператор = присваивает значения, а == сравнивает их', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (12, 1),
       (12, 6),
       (12, 4),
       (12, 5);




--13 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (13, true, 'Какие из представленных символов или их комбинаций используются в Python для комментирования кода?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(38, 13, 'end', false),
	(39, 13, 'sep', true),
        (40, 13, 'вывод в столбик невозможен', false),
        (41, 13, 'значения аргументов и так всегда выводятся с новой строки', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (13, 1),
       (13, 6);




--14 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (14, true, 'Данные какого типа возвращает встроенная функция input()? ', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(42, 14, 'логический тип', false),
	(43, 14, 'строка', true),
        (44, 14, 'целое число', false),
        (45, 14, 'вещественное число', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (14, 1),
       (14, 6);





--15 Python Junior Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (15, true, 'Какой стиль именования классов рекомендуется в руководстве PEP 8 по оформлению кода на Python?', false, NULL, now(), false, all_author_id);

INSERT INTO answer (id, question_id, text, is_true)
VALUES 	(46, 15, 'camelCase', false),
	(47, 15, 'CapWords', true),
        (48, 15, 'snake_case', false),
        (49, 15, 'ALL_CAPS', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (15, 1),
       (15, 6),
       (15, 2);




--16 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (16, true, 'Что такое замыкание?', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (16, 6),
       (16, 2);



--17 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (17, true, 'Что такое итератор?', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (17, 6),
       (17, 2);




--18 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (18, true, 'Что такое генераторная функция?', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (18, 6),
       (18, 2);



--19 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (19, true, 'Что возвращает итерация по словарю?', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (19, 6),
       (19, 2);



--20 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (20, true, 'Что такое магические методы, для чего нужны?', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (20, 6),
       (20, 2);



--21 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (21, true, 'Что такое контекстный менеджер? Как написать свой?', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (21, 6),
       (21, 2);



--22 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (22, true, 'Что такое __slots__. Плюсы, минусы', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (22, 6),
       (22, 2);



--23 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (23, true, 'В чем смысл параметров _value, __value', true, NULL, now(), false, all_author_id);


INSERT INTO question_tags (question_id, tag_id)
VALUES (23, 6),
       (23, 2);


--24 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (24, true, 'Для чего могут применять конструкцию try finally без except', true, NULL, now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (24, 2),
       (24, 6);


--25 Python Senior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (25, true, 'Что такое сцепление исключений', true, NULL, now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (25, 3),
       (25, 6);


--26 Python Senior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (26, true, 'Какие есть классы исключений', true, NULL, now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (26, 3),
       (26, 6);


--27 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (27, true, 'Что может быть декоратором. К чему может быть применен декоратор', true, NULL, now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (27, 2),
       (27, 6);


--28 Java Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (28, true, 'Как называется код между фигурными скобками?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (50, 28, 'функция', false),
       (51, 28, 'секция', false),
       (52, 28, 'тело', false),
       (53, 28, 'блок', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (28, 1),
       (28, 4),
       (28, 6);


--29 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (29, true, 'Каково значение arr[1].length в следующем массиве?
int[][] arr = { {1, 2, 3, 4, 5}, { 6, 7, 8, 9, 10} };', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (54, 29, '5', true),
       (55, 29, '10', false),
       (56, 29, '1', false),
       (57, 29, '2', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (29, 1),
       (29, 4);


--30 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (30, true, 'В чем разница между char и Character?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (58, 30, 'char является примитивным типом, а Character классом.', true),
       (59, 30, 'нет разницы, они оба примитивные типы', false),
       (60, 30, 'char является классом, а Character примитивным типом.', false),
       (61, 30, 'нет разницы, они оба классы.', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (30, 1),
       (30, 4);


--31 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (31, true, 'Какие из этих операторов можно использовать для объединения двух или более объектов String?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (62, 31, '+', true),
       (63, 31, '*=', false),
       (64, 31, '+=', false),
       (65, 31, '*', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (31, 1),
       (31, 4);


--32 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (32, true, 'От какого класса наследуют все классы Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (66, 32, 'Object', true),
       (67, 32, 'List', false),
       (68, 32, 'Runtime', false),
       (69, 32, 'Collection', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (32, 1),
       (32, 4);


--33 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (33, true, 'Укажите допустимый синтаксис комментария.', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (70, 33, ' /* Комментарий */', true),
       (71, 33, ' # Комментарий', false),
       (72, 33, ' /* Комментарий', false),
       (73, 33, '// Комментарий', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (33, 1),
       (33, 4);


--34 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (34, true, 'Укажите допустимый синтаксис комментария.', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (74, 34, 'int', false),
       (75, 34, 'float', false),
       (76, 34, 'string', true),
       (77, 34, 'unknown', true),
       (78, 34, 'Double', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (34, 1),
       (34, 4);


--35 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (35, true, 'Можно ли создать программу (приложение) на Java, не используя среду разработки (IDE)?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (79, 35, 'Да', true),
       (80, 35, 'Нет, так как необходимо скомпилировать исходный код в байт-код', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (35, 1),
       (35, 4);


--36 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (36, true, 'Какое расширение имеют файлы с исходным кодом Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (81, 36, 'javac', false),
       (82, 36, 'java', true),
       (83, 36, 'class', false),
       (84, 36, 'classpath', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (36, 1),
       (36, 4);


--37 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (37, true, 'Может ли файл содержать более одного класса Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (85, 37, 'Да, но только если один внешний класс имеет модификатор доступа public', true),
       (86, 37, 'Да, если все внешние классы будут иметь модификатор доступа private', false),
       (87, 37, 'Нет', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (37, 1),
       (37, 4);


--38 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (38, true, 'Какое имя переменной является синтаксически недопустимым?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (88, 38, '53someVariable', true),
       (89, 38, '_someVariable', false),
       (90, 38, ' some-variable', true),
       (91, 38, 'somevariable', false),
       (92, 38, 'someVariable', false),
       (93, 38, 'Somevariable53', false),
       (94, 38, '_53someVariable', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (38, 1),
       (38, 4);


--39 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (39, true, 'Импорт какого пакета в Java происходит автоматически?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (95, 39, 'Все пакеты нужно явно указывать', false),
       (96, 39, 'java.util', false),
       (97, 39, 'java.lang', true),
       (98, 39, 'java.text', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (39, 1),
       (39, 4);



END; $$ LANGUAGE plpgsql;