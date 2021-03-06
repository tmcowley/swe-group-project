-- RESET, DROPS (order important)
DROP TABLE IF EXISTS participant_in_event;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS archived_event;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS template;
DROP TABLE IF EXISTS host;

DROP EXTENSION IF EXISTS citext;
DROP TYPE IF EXISTS event_type;


-- FUNCTIONS (HAS RETURN), PROCEDURES (MANIPULATES, NO RETURN)


-- ENUMS, EXTENSIONS
CREATE EXTENSION citext;
CREATE TYPE event_type 
    AS ENUM 
    ('lecture', 'seminar', 'conference', 'workshop', 'other');

------------------
 --   SCHEMA   --
------------------

CREATE TABLE host(
    host_id         SERIAL          NOT NULL,
    host_code       VARCHAR(64)
                    UNIQUE          NOT NULL,
    ip_address      INET            NOT NULL,
    e_address       citext UNIQUE   NOT NULL,
    f_name          VARCHAR(35)     NOT NULL,
    l_name          VARCHAR(35)     NOT NULL,
    sys_ban         BOOLEAN         NOT NULL
                    DEFAULT FALSE,
    PRIMARY KEY (host_id)
);

CREATE TABLE template(
    template_id     SERIAL          NOT NULL,
    host_id         INT             NOT NULL,
    template_code   VARCHAR(6)
                    UNIQUE          NOT NULL
                    -- simulate lower-case alphanumeric
                    CHECK (template_code ~* '^[a-z0-9]+$'),
    data            VARCHAR(200)    NOT NULL,
    FOREIGN KEY (host_id) 
        REFERENCES host(host_id) 
        ON DELETE CASCADE,
    PRIMARY KEY (template_id)
);

CREATE TABLE participant(
    participant_id  SERIAL          NOT NULL,
    ip_address      INET            NOT NULL,
    f_name          VARCHAR(35)     NOT NULL,
    l_name          VARCHAR(35)     NOT NULL,
    sys_ban         BOOLEAN         NOT NULL
                    DEFAULT FALSE,
    PRIMARY KEY (participant_id)
);

CREATE TABLE event(
    event_id        SERIAL          NOT NULL,
    host_id         INT             NOT NULL,
    template_id     INT,
    title           VARCHAR(32),
    description     VARCHAR(128),
    type            VARCHAR(10)     CHECK (type = 'lecture' or
                                           type = 'seminar' or
                                           type = 'conference' or
                                           type = 'workshop' or
                                           type = 'other'),
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    event_code      VARCHAR(4) 
                    UNIQUE          NOT NULL
                    -- simulate lower-case alphanumeric
                    CHECK (event_code ~* '^[a-z0-9]+$'), 
    FOREIGN KEY (template_id) 
                    REFERENCES template(template_id) 
                    ON DELETE SET NULL,
    FOREIGN KEY (host_id) 
                    REFERENCES host(host_id) 
                    ON DELETE CASCADE,
    PRIMARY KEY (event_id)
);

CREATE TABLE archived_event(
    event_id        SERIAL          NOT NULL,
    host_id         INT             NOT NULL,
    total_mood      VARCHAR(40)     NOT NULL,
    title           VARCHAR(32)     NOT NULL,
    description     VARCHAR(128)    NOT NULL,
    type            VARCHAR(10)     CHECK (type = 'lecture' or
                                           type = 'seminar' or
                                           type = 'conference' or
                                           type = 'workshop' or
                                           type = 'other'),
    start_time      TIMESTAMP       NOT NULL,
    end_time        TIMESTAMP       NOT NULL,
    FOREIGN KEY (host_id) 
        REFERENCES host(host_id) 
        ON DELETE CASCADE,
    PRIMARY KEY (event_id)
);

CREATE TABLE feedback(
    feedback_id     SERIAL          NOT NULL,
    participant_id  INT             NOT NULL,
    event_id        INT             NOT NULL,
    -- data            VARCHAR(200)    NOT NULL,
    -- sentiment       VARCHAR(40)     NOT NULL,
    results         TEXT [],
    weights         REAL [],
    type            INT [],
    key             INT [],
    compound        REAL,
    key_results     TEXT [],
    anonymous       BOOLEAN         NOT NULL
                    DEFAULT FALSE,
    time_stamp      TIMESTAMP       NOT NULL,
    FOREIGN KEY (participant_id) 
        REFERENCES participant(participant_id) 
        ON DELETE CASCADE,
    FOREIGN KEY (event_id) 
        REFERENCES event(event_id) 
        ON DELETE CASCADE,
    PRIMARY KEY (feedback_id)
);

CREATE TABLE participant_in_event(
    participant_id  INT         NOT NULL,
    event_id        INT         NOT NULL,
    muted           BOOLEAN     NOT NULL
                    DEFAULT FALSE,
    FOREIGN KEY (participant_id) 
        REFERENCES participant(participant_id) 
        ON DELETE CASCADE,
    FOREIGN KEY (event_id) 
        REFERENCES event(event_id) 
        ON DELETE CASCADE,
    PRIMARY KEY (participant_id,event_id)
);
