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
    ipAddress       INET             NOT NULL,
    fName           VARCHAR(35)      NOT NULL,
    lName           VARCHAR(35)      NOT NULL,
    sysBan          BOOLEAN          DEFAULT 0,
    PRIMARY KEY (participantID)
);

CREATE TABLE host(
    hostID          SERIAL           NOT NULL,
    ipAddress       INET             NOT NULL,
    eAddress        citext UNIQUE    NOT NULL,
    fName           VARCHAR(35)      NOT NULL,
    lName           VARCHAR(35)      NOT NULL,
    sysBan          BOOLEAN          DEFAULT 0,
    PRIMARY KEY (hostID)
);

CREATE TABLE event(
    eventID       SERIAL               NOT NULL,
    hostID        SERIAL               NOT NULL,
    templateID    SERIAL               NOT NULL,
    title         VARCHAR(32)          NOT NULL,
    description   VARCHAR(128)         NOT NULL,
    type          eventType            NOT NULL,
    startTime     TIMESTAMP            NOT NULL,
    endTime       TIMESTAMP            NOT NULL,
    eventCode     VARCHAR(4)           CHECK (eventCode ~* '^[A-Z0-9]+$'), -- simulate upper alphanumeric
    FOREIGN KEY (templateID) REFERENCES template(templateID) ON DELETE CASCADE,
    FOREIGN KEY (hostID) REFERENCES host(hostID) ON DELETE CASCADE,
    PRIMARY KEY (eventID)
);

CREATE TABLE archivedEvent(
    eventID       SERIAL               NOT NULL,
    hostID        SERIAL               NOT NULL,
    totalMood     VARCHAR(40)          NOT NULL,
    title         VARCHAR(32)          NOT NULL,
    description   VARCHAR(128)         NOT NULL,
    type          eventType            NOT NULL,
    startTime     TIMESTAMP            NOT NULL,
    endTime       TIMESTAMP            NOT NULL,
    FOREIGN KEY (hostID) REFERENCES host(hostID) ON DELETE CASCADE,
    PRIMARY KEY (eventID)
);

CREATE TABLE feedback(
    feedbackID        SERIAL         NOT NULL,
    participantID     SERIAL         NOT NULL,
    eventID           SERIAL         NOT NULL,
    data              VARCHAR(200)   NOT NULL,
    sentiment         VARCHAR(40)    NOT NULL,
    anonymous         BOOLEAN        DEFAULT 0,
    timestamp         TIMESTAMP      NOT NULL,
    FOREIGN KEY (participantID) REFERENCES participant(participantID) ON DELETE CASCADE,
    FOREIGN KEY (eventID) REFERENCES event(eventID) ON DELETE CASCADE,
    PRIMARY KEY (feedbackID)
);

CREATE TABLE template(
    templateID      SERIAL           NOT NULL,
    hostID          SERIAL           NOT NULL,
    data            VARCHAR(200)     NOT NULL,
    FOREIGN KEY (hostID) REFERENCES host(hostID) ON DELETE CASCADE,
    PRIMARY KEY (templateID)
);

CREATE TABLE participantInEvent(
    participantID      SERIAL           NOT NULL,
    eventID            SERIAL           NOT NULL,
    muted              BOOLEAN          DEFAULT 0,
    FOREIGN KEY (participantID) REFERENCES participant(participantID) ON DELETE CASCADE,
    FOREIGN KEY (eventID) REFERENCES event(eventID) ON DELETE CASCADE,
    PRIMARY KEY (participantID,eventID)
);
