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
