-- Создание основных таблиц
CREATE TYPE tag_category AS ENUM (
    'Difficulty',
    'Language');

CREATE TABLE if not exists question
(
    id            SERIAL PRIMARY KEY,
    checked       BOOLEAN DEFAULT false NOT NULL,
    text          TEXT                  NOT NULL,
    is_open       BOOLEAN DEFAULT false NOT NULL,
    comment       TEXT,
    creation_date TIMESTAMP             NOT NULL,
    is_deleted    BOOLEAN DEFAULT false NOT NULL,
    user_id       INTEGER               NOT NULL
);

CREATE SEQUENCE if not exists question_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
ALTER SEQUENCE question_id_seq OWNED BY question.id;

CREATE TABLE if not exists answer
(
    id          SERIAL PRIMARY KEY,
    question_id INTEGER               NOT NULL REFERENCES question (id) ON DELETE CASCADE,
    text        TEXT                  NOT NULL,
    is_true     BOOLEAN DEFAULT false NOT NULL
);

CREATE SEQUENCE if not exists answer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

ALTER SEQUENCE answer_id_seq OWNED BY answer.id;


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

CREATE TABLE if not exists "user"
(
    id        bigint PRIMARY KEY,
    is_banned BOOLEAN DEFAULT false NOT NULL,
    rating    INTEGER DEFAULT 0     NOT NULL
);


CREATE TABLE if not exists user_answers
(
    user_id     INTEGER               NOT NULL,
    question_id INTEGER               NOT NULL,
    is_true     BOOLEAN DEFAULT false NOT NULL,
    text        TEXT                  NOT NULL
);

CREATE TABLE if not exists user_tags
(
    user_id INTEGER NOT NULL,
    tag_id  INTEGER NOT NULL
);


ALTER TABLE ONLY answer
    ALTER COLUMN id SET DEFAULT nextval('answer_id_seq'::regclass);
ALTER TABLE ONLY question
    ALTER COLUMN id SET DEFAULT nextval('question_id_seq'::regclass);
ALTER TABLE ONLY tag
    ALTER COLUMN id SET DEFAULT nextval('tag_id_seq'::regclass);
