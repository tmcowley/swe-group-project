-- RESET, DROPS


-- FUNCTIONS (HAS RETURN), PROCEDURES (MANIPULATES, NO RETURN)


-- ENUMS, EXTENSIONS
CREATE EXTENSION citext;
CREATE TYPE eventType AS ENUM ('lecture', 'seminar', 'conference', 'workshop', 'other');

------------------
 --   SCHEMA   --
------------------

CREATE TABLE participant(
    participantID   SERIAL           NOT NULL,
    address         INET             NOT NULL,
    fName           VARCHAR(35)      NOT NULL,
    lName           VARCHAR(35)      NOT NULL,
    sysBan          BOOLEAN          DEFAULT 0,
    PRIMARY KEY (participantID)
);

CREATE TABLE host(
    hostID          SERIAL           NOT NULL,
    address         INET             NOT NULL,
    eAddress        citext UNIQUE    NOT NULL,
    fName           VARCHAR(35)      NOT NULL,
    lName           VARCHAR(35)      NOT NULL,
    sysBan          BOOLEAN          DEFAULT 0,
    PRIMARY KEY (hostID)
);

CREATE TABLE event(
    eventID       SERIAL               NOT NULL,
    hostID        SERIAL               NOT NULL,
    title         VARCHAR(32)          NOT NULL,
    description   VARCHAR(128)         NOT NULL,
    type          eventType            NOT NULL,
    eventCode     VARCHAR(4)           CHECK (eventCode ~* '^[A-Z0-9]+$'), -- simulate upper alphanumeric
    FOREIGN KEY (hostID) REFERNCES host(hostID) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (eventID)
);

CREATE TABLE feedback(
    feedbackID        SERIAL         NOT NULL,
    participantID     SERIAL         NOT NULL,
    eventID           SERIAL         NOT NULL,
    data              VARCHAR(200)   NOT NULL,
    mood              VARCHAR(40)    NOT NULL,
    sentiment         VARCHAR(40)    NOT NULL,
    FOREIGN KEY (participantID) REFERENCES participant(participantID) ON DELETE CASCADE,
    FOREIGN KEY (eventID) REFERENCES event(eventID) ON DELETE CASCADE,
    PRIMARY KEY (feedbackID)
);

CREATE TABLE template(
    templateID      SERIAL           NOT NULL,
    eventID         SERIAL           NOT NULL,
    data            VARCHAR(200)     NOT NULL,
    FOREIGN KEY (eventID) REFERENCES event(eventID) ON DELETE CASCADE,
    PRIMARY KEY (templateID)
);

CREATE TABLE participantInEvent(
    participantID      SERIAL           NOT NULL,
    eventID            SERIAL           NOT NULL,
    FOREIGN KEY (participantID) REFERENCES participant(participantID) ON DELETE CASCADE,
    FOREIGN KEY (eventID) REFERENCES event(eventID) ON DELETE CASCADE,
    PRIMARY KEY (participantID,eventID)
);

COMMENT ON TABLE event IS 'storage of an event';
