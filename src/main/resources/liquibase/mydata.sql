--Заполняет БД данными

DO $$
DECLARE
eugen_id integer := 1273082747;
nastya_id integer := 1051467318;
igor_id integer := 1481223955;
kirill_id integer := 520249743;
andrew_id integer := 810518543;
all_author_id integer := eugen_id;
BEGIN

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


--24 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (24, true, 'Выберите верные утверждения о языке программирования Python', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (50, 24, 'высокоуровневый', true),
       (51, 24, 'низкоуровневый', false),
       (52, 24, 'интерпретируемый', true),
       (53, 24, 'компилируемый', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (24, 1),
       (24, 6);


--25 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (25, true, 'Обязательно ли в Пайтоне объявлять тип переменной?', false, 'В Python не обязательно объявлять тип переменной. Python является языком с динамической типизацией, что означает, что переменные определяются автоматически на основе их значения при присваивании. Это позволяет избежать явного объявления типа переменной и упрощает код.', now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (54, 25, 'Да', false),
       (55, 25, 'Нет', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (25, 1),
       (25, 6);


--26 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (26, true, 'Какие из представленных фрагментов кода являются литералами?', false,  '  ''#'' являеется строковым литералом,  0.77 - числовым литералом, [1, 2] - литералом списка.
5**2 - выражение, а не литерал.', now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (56, 26, '  ''#'' ', true),
       (57, 26, '5**2', false),
       (58, 26, '[1, 2]', true),
       (59, 26, '0.77', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (26, 1),
       (26, 6);


--27 Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (27, true, 'Какие из предлагаемых имен допустимы в Python?', false, ' В Python допустимы имена переменных, которые соответствуют следующим правилам:
Имя переменной должно начинаться с буквы или символа подчёркивания (_).
Последующие символы могут быть буквами, цифрами или символами подчёркивания.
Имена переменных чувствительны к регистру. Например, a и A — это разные переменные.
В качестве имён переменных нельзя использовать ключевые слова Python (например, if, for, while и т. д.).', now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (60, 27, ' s_5 ', true),
       (61, 27, '_5s', false),
       (62, 27, '5_s', false),
       (63, 27, 'Щ5', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (27, 1),
       (27, 6);


--28 Python Java C++ Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (28, true, 'Как будет выглядеть имя переменной в верблюжьей нотации при использовании фразы «show must go on»?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (64, 28, 'showMustGoOn', true),
       (65, 28, 'ShowMustGoOn', false),
       (66, 28, 'show_must_go_on', false),
       (67, 28, 'SHOW_MUST_GO_ON', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (28, 1),
       (28, 4),
       (28, 5),
       (28, 6);


--29 Python Java C++ Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (29, true, 'Что по умолчанию служит концом инструкции в Python?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (68, 29, 'конец строки', true),
       (69, 29, 'запятая', false),
       (70, 29, 'точка', false),
       (71, 29, 'точка с запятой', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (29, 1),
       (29, 6);


--30 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (30, true, 'Для чего могут применять конструкцию try finally без except', true,  ' Если в блоке try произойдет ошибка, то блок finally все-равно будет выполнен и внутри него можно будет сделать "cleanup", например. ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (30, 2),
       (30, 6);


--31 Python Senior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (31, true, 'Что такое сцепление исключений', true,  ' В Python 3 при возбуждении исключения в блоке except, старое исключение сохраняется в атрибуте данных __context__ и если новое исключение не обработано, то будет выведена информация о том, что новое исключение возникло при обработке старого ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (31, 3),
       (31, 6);


--32 Python Senior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (32, true, 'Какие есть классы исключений', true,  ' BaseException, Exception, ArithmeticError, BufferError, LookupError ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (32, 3),
       (32, 6);


--33 Python Middle
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (33, true, 'Что может быть декоратором. К чему может быть применен декоратор', true,  ' Декоратором может быть любой вызываемый объект: функция, лямбда, класс, экземпляр класса. В последнем случае определите метод __call__.
Применять декоратор можно к любому объекту. Чаще всего к функциям, методам и классам. Декорирование встречается настолько часто, что под него выделен особый оператор @. ' , now(), false, all_author_id);

INSERT INTO question_tags (question_id, tag_id)
VALUES (33, 2),
       (33, 6);


--34 Java Python Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (34, true, 'Как называется код между фигурными скобками?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (72, 34, 'функция', false),
       (73, 34, 'секция', false),
       (74, 34, 'тело', false),
       (75, 34, 'блок', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (34, 1),
       (34, 4),
       (34, 6);


--35 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (35, true, 'Каково значение arr[1].length в следующем массиве?
int[][] arr = { {1, 2, 3, 4, 5}, { 6, 7, 8, 9, 10} };', false,  ' Будет посчитана длина второго массива. ' , now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (76, 35, '5', true),
       (77, 35, '10', false),
       (78, 35, '1', false),
       (79, 35, '2', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (35, 1),
       (35, 4);


--36 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (36, true, 'В чем разница между char и Character?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (80, 36, 'char является примитивным типом, а Character классом.', true),
       (81, 36, 'нет разницы, они оба примитивные типы', false),
       (82, 36, 'char является классом, а Character примитивным типом.', false),
       (83, 36, 'нет разницы, они оба классы.', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (36, 1),
       (36, 4);


--37 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (37, true, 'Какие из этих операторов можно использовать для объединения двух или более объектов String?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (84, 37, '+', true),
       (85, 37, '*=', false),
       (86, 37, '+=', false),
       (87, 37, '*', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (37, 1),
       (37, 4);


--38 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (38, true, 'От какого класса наследуют все классы Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (88, 38, 'Object', true),
       (89, 38, 'List', false),
       (90, 38, 'Runtime', false),
       (91, 38, 'Collection', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (38, 1),
       (38, 4);


--39 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (39, true, 'Укажите допустимый синтаксис комментария.', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (92, 39, ' /* Комментарий */', true),
       (93, 39, ' # Комментарий', false),
       (94, 39, ' /* Комментарий', false),
       (95, 39, '// Комментарий', true);

INSERT INTO question_tags (question_id, tag_id)
VALUES (39, 1),
       (39, 4);


--40 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (40, true, 'Укажите допустимый тип данных.', false,  ' unknown - не тип, Double - класс, а не тип ' , now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (96, 40, 'int', false),
       (97, 40, 'float', false),
       (98, 40, 'string', true),
       (99, 40, 'unknown', true),
       (100, 40, 'Double', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (40, 1),
       (40, 4);


--41 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (41, true, 'Можно ли создать программу (приложение) на Java, не используя среду разработки (IDE)?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (101, 41, 'Да', true),
       (102, 41, 'Нет, так как необходимо скомпилировать исходный код в байт-код', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (41, 1),
       (41, 4);


--42 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (42, true, 'Какое расширение имеют файлы с исходным кодом Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (103, 42, 'javac', false),
       (104, 42, 'java', true),
       (105, 42, 'class', false),
       (106, 42, 'classpath', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (42, 1),
       (42, 4);


--43 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (43, true, 'Может ли файл содержать более одного класса Java?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (107, 43, 'Да, но только если один внешний класс имеет модификатор доступа public', true),
       (108, 43, 'Да, если все внешние классы будут иметь модификатор доступа private', false),
       (109, 43, 'Нет', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (43, 1),
       (43, 4);


--44 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (44, true, 'Какое имя переменной является синтаксически недопустимым?', false,  ' Имя переменной должно начинаться с буквы латинского алфавита или знака подчёркивания, и состоять из букв латинского алфавита, знака подчёркивания и цифр. ' , now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (110, 44, '53someVariable', true),
       (111, 44, '_someVariable', false),
       (112, 44, ' some-variable', true),
       (113, 44, 'somevariable', false),
       (114, 44, 'someVariable', false),
       (115, 44, 'Somevariable53', false),
       (116, 44, '_53someVariable', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (44, 1),
       (44, 4);


--45 Java Junior
INSERT INTO question (id, checked, text, is_open, comment, creation_date, is_deleted, user_id)
VALUES (45, true, 'Импорт какого пакета в Java происходит автоматически?', false, NULL, now(), false, all_author_id);

INSERT INTO answer  (id, question_id, text, is_true)
VALUES (117, 45, 'Все пакеты нужно явно указывать', false),
       (118, 45, 'java.util', false),
       (119, 45, 'java.lang', true),
       (120, 45, 'java.text', false);

INSERT INTO question_tags (question_id, tag_id)
VALUES (45, 1),
       (45, 4);



create sequence if not exists question_id_seq;
alter sequence question_id_seq restart with 46;

create sequence if not exists answer_id_seq;
alter sequence answer_id_seq restart with 121;

create sequence if not exists tag_id_seq;
alter sequence tag_id_seq restart with 7;

create sequence if not exists user_answers_id_seq;
alter sequence user_answers_id_seq restart with 4;


END; $$ LANGUAGE plpgsql;
