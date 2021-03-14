-- RESET, DROPS (order important)
DROP TABLE IF EXISTS participant_in_event;
DROP TABLE IF EXISTS component_in_template;
DROP TABLE IF EXISTS template_component;
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
-- CREATE TYPE event_type 
--     AS ENUM 
--     ('lecture', 'seminar', 'conference', 'workshop', 'other');

-- CREATE TYPE tc_type 
--     AS ENUM 
--     ('text', 'radio', 'checkbox');

------------------
 --   SCHEMA   --
------------------

-- create host table which stores hosts data
CREATE TABLE host(
    host_id         SERIAL          NOT NULL,
    host_code       VARCHAR(64)
                    UNIQUE          NOT NULL,
    e_address       citext UNIQUE   NOT NULL,
    f_name          VARCHAR(64)     NOT NULL,
    l_name          VARCHAR(64)     NOT NULL,
    sys_ban         BOOLEAN         NOT NULL
                    DEFAULT FALSE,
    PRIMARY KEY (host_id)
);

-- create template table which stores templates created by hosts
CREATE TABLE template(
    template_id     SERIAL          NOT NULL,
    host_id         INT             NOT NULL,
    template_code   VARCHAR(6)
                    UNIQUE          NOT NULL
                    -- simulate lower-case alphanumeric
                    CHECK (template_code ~* '^[a-z0-9]+$'),
    template_name   TEXT            NOT NULL,
    timestamp       TIMESTAMP       NOT NULL,
    FOREIGN KEY (host_id) 
        REFERENCES host(host_id) 
        ON DELETE CASCADE,
    PRIMARY KEY (template_id)
);

-- create component table which stores components data
CREATE TABLE template_component(
    tc_id           SERIAL          NOT NULL,
    tc_name         TEXT            NOT NULL,
    tc_type         TEXT            
                    CHECK (tc_type IN ('question', 'radio', 'checkbox')),
    tc_prompt       TEXT            NOT NULL
                    DEFAULT FALSE,
    tc_considered_in_sentiment 
                    BOOLEAN 
                    DEFAULT TRUE,
    tc_sentiment_weight
                    INT 
                    DEFAULT 5,
    tc_options      TEXT[],
    tc_options_ans  BOOLEAN[],
    tc_options_pos  INT[],
    tc_text_response TEXT,
    PRIMARY KEY (tc_id)
);

-- create template component link table which stores components contained in templates
CREATE TABLE component_in_template(
    component_id    INTEGER         NOT NULL,
    template_id     INTEGER         NOT NULL,
    FOREIGN KEY (component_id)
        REFERENCES template_component(tc_id)
        ON DELETE CASCADE,
    FOREIGN KEY (template_id)
        REFERENCES template(template_id)
        ON DELETE CASCADE,
    PRIMARY KEY(component_id, template_id)
);

-- create participant table which stores participants data
CREATE TABLE participant(
    participant_id  SERIAL          NOT NULL,
    ip_address      INET,
    f_name          VARCHAR(64)     NOT NULL,
    l_name          VARCHAR(64)     NOT NULL,
    sys_ban         BOOLEAN         NOT NULL
                    DEFAULT FALSE,
    PRIMARY KEY (participant_id)
);

-- create event table which stores events data
CREATE TABLE event(
    event_id        SERIAL          NOT NULL,
    host_id         INT             NOT NULL,
    template_id     INT,
    title           TEXT,
    description     TEXT,
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

-- create archived event table which stores archived events data
-- archived events are not functional within the system
CREATE TABLE archived_event(
    event_id        SERIAL          NOT NULL,
    host_id         INT             NOT NULL,
    total_mood      VARCHAR(40)     NOT NULL,
    title           VARCHAR(32)     NOT NULL,
    description     TEXT            NOT NULL,
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

-- create feedback table which stores feedbacks data
CREATE TABLE feedback(
    feedback_id     SERIAL          NOT NULL,
    participant_id  INT             NOT NULL,
    event_id        INT             NOT NULL,
    results         TEXT[],
    weights         REAL[],
    types           bytea,
    keys            BOOLEAN[],
    sub_weights     bytea[],
    compound        REAL,
    key_results     TEXT[],
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

-- create participant event link table which stores participants joined in events
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
