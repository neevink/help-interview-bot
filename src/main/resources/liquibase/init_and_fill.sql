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
);INSERT INTO bot_user (id, is_banned, rating)
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


--4 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (4, true, 'Выберите верные утверждения о языке программирования Python', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (7, 4, 'высокоуровневый', true),
       (8, 4, 'низкоуровневый', false),
       (9, 4, 'интерпретируемый', true),
       (10, 4, 'компилируемый', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (4, 1),
       (4, 6);


--5 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (5, true, 'Обязательно ли в Пайтоне объявлять тип переменной?', false, 'В Python не обязательно объявлять тип переменной. Python является языком с динамической типизацией, что означает, что переменные определяются автоматически на основе их значения при присваивании. Это позволяет избежать явного объявления типа переменной и упрощает код.', now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (11, 5, 'Да', false),
       (12, 5, 'Нет', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (5, 1),
       (5, 6);


--6 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (6, true, 'Какие из представленных фрагментов кода являются литералами?', false,  '  ''#'' являеется строковым литералом,  0.77 - числовым литералом, [1, 2] - литералом списка.
5**2 - выражение, а не литерал.', now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (13, 6, '  ''#'' ', true),
       (14, 6, '5**2', false),
       (15, 6, '[1, 2]', true),
       (16, 6, '0.77', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (6, 1),
       (6, 6);


--7 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (7, true, 'Какие из предлагаемых имен допустимы в Python?', false, ' В Python допустимы имена переменных, которые соответствуют следующим правилам: 
Имя переменной должно начинаться с буквы или символа подчёркивания (_). 
Последующие символы могут быть буквами, цифрами или символами подчёркивания.
Имена переменных чувствительны к регистру. Например, a и A — это разные переменные.
В качестве имён переменных нельзя использовать ключевые слова Python (например, if, for, while и т. д.).', now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (17, 7, ' s_5 ', true),
       (18, 7, '_5s', false),
       (19, 7, '5_s', false),
       (20, 7, 'Щ5', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (7, 1),
       (7, 6);


--8 Python Java C++ Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (8, true, 'Как будет выглядеть имя переменной в верблюжьей нотации при использовании фразы «show must go on»?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (21, 8, 'showMustGoOn', true),
       (22, 8, 'ShowMustGoOn', false),
       (23, 8, 'show_must_go_on', false),
       (24, 8, 'SHOW_MUST_GO_ON', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (8, 1),
       (8, 4),
       (8, 5),
       (8, 6);


--9 Python Java C++ Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (9, true, 'Что по умолчанию служит концом инструкции в Python?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (25, 9, 'конец строки', true),
       (26, 9, 'запятая', false),
       (27, 9, 'точка', false),
       (28, 9, 'точка с запятой', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (9, 1),
       (9, 6);


--10 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (10, true, 'Для чего могут применять конструкцию try finally без except', true,  ' Если в блоке try произойдет ошибка, то блок finally все-равно будет выполнен и внутри него можно будет сделать "cleanup", например. ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (10, 2),
       (10, 6);


--11 Python Senior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (11, true, 'Что такое сцепление исключений', true,  ' В Python 3 при возбуждении исключения в блоке except, старое исключение сохраняется в атрибуте данных __context__ и если новое исключение не обработано, то будет выведена информация о том, что новое исключение возникло при обработке старого ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (11, 3),
       (11, 6);


--12 Python Senior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (12, true, 'Какие есть классы исключений', true,  ' BaseException, Exception, ArithmeticError, BufferError, LookupError ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (12, 3),
       (12, 6);


--13 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (13, true, 'Что может быть декоратором. К чему может быть применен декоратор', true,  ' Декоратором может быть любой вызываемый объект: функция, лямбда, класс, экземпляр класса. В последнем случае определите метод __call__. 
Применять декоратор можно к любому объекту. Чаще всего к функциям, методам и классам. Декорирование встречается настолько часто, что под него выделен особый оператор @. ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (13, 2),
       (13, 6);


--14 Java Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (14, true, 'Как называется код между фигурными скобками?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (29, 14, 'функция', false),
       (30, 14, 'секция', false),
       (31, 14, 'тело', false),
       (32, 14, 'блок', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (14, 1),
       (14, 4),
       (14, 6);


--15 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (15, true, 'Каково значение arr[1].length в следующем массиве?
int[][] arr = { {1, 2, 3, 4, 5}, { 6, 7, 8, 9, 10} };', false,  ' Будет посчитана длина второго массива. ' , now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (33, 15, '5', true),
       (34, 15, '10', false),
       (35, 15, '1', false),
       (36, 15, '2', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (15, 1),
       (15, 4);


--16 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (16, true, 'В чем разница между char и Character?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (37, 16, 'char является примитивным типом, а Character классом.', true),
       (38, 16, 'нет разницы, они оба примитивные типы', false),
       (39, 16, 'char является классом, а Character примитивным типом.', false),
       (40, 16, 'нет разницы, они оба классы.', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (16, 1),
       (16, 4);


--17 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (17, true, 'Какие из этих операторов можно использовать для объединения двух или более объектов String?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (41, 17, '+', true),
       (42, 17, '*=', false),
       (43, 17, '+=', false),
       (44, 17, '*', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (17, 1),
       (17, 4);


--18 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (18, true, 'От какого класса наследуют все классы Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (45, 18, 'Object', true),
       (46, 18, 'List', false),
       (47, 18, 'Runtime', false),
       (48, 18, 'Collection', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (18, 1),
       (18, 4);


--19 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (19, true, 'Укажите допустимый синтаксис комментария.', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (49, 19, ' /* Комментарий */', true),
       (50, 19, ' # Комментарий', false),
       (51, 19, ' /* Комментарий', false),
       (52, 19, '// Комментарий', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (19, 1),
       (19, 4);


--20 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (20, true, 'Укажите допустимый тип данных.', false,  ' unknown - не тип, Double - класс, а не тип ' , now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (53, 20, 'int', false),
       (54, 20, 'float', false),
       (55, 20, 'string', true),
       (56, 20, 'unknown', true),
       (57, 20, 'Double', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (20, 1),
       (20, 4);


--21 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (21, true, 'Можно ли создать программу (приложение) на Java, не используя среду разработки (IDE)?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (58, 21, 'Да', true),
       (59, 21, 'Нет, так как необходимо скомпилировать исходный код в байт-код', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (21, 1),
       (21, 4);


--22 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (22, true, 'Какое расширение имеют файлы с исходным кодом Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (60, 22, 'javac', false),
       (61, 22, 'java', true),
       (62, 22, 'class', false),
       (63, 22, 'classpath', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (22, 1),
       (22, 4);


--23 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (23, true, 'Может ли файл содержать более одного класса Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (64, 23, 'Да, но только если один внешний класс имеет модификатор доступа public', true),
       (65, 23, 'Да, если все внешние классы будут иметь модификатор доступа private', false),
       (66, 23, 'Нет', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (23, 1),
       (23, 4);


--24 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (24, true, 'Какое имя переменной является синтаксически недопустимым?', false,  ' Имя переменной должно начинаться с буквы латинского алфавита или знака подчёркивания, и состоять из букв латинского алфавита, знака подчёркивания и цифр. ' , now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (67, 24, '53someVariable', true),
       (68, 24, '_someVariable', false),
       (69, 24, ' some-variable', true),
       (70, 24, 'somevariable', false),
       (71, 24, 'someVariable', false),
       (72, 24, 'Somevariable53', false),
       (73, 24, '_53someVariable', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (24, 1),
       (24, 4);


--25 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (25, true, 'Импорт какого пакета в Java происходит автоматически?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (74, 25, 'Все пакеты нужно явно указывать', false),
       (75, 25, 'java.util', false),
       (76, 25, 'java.lang', true),
       (77, 25, 'java.text', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (25, 1),
       (25, 4);


END; $$ LANGUAGE plpgsql;
