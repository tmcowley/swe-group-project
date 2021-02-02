-- RESET, DROPS


-- FUNCTIONS (HAS RETURN), PROCEDURES (MANIPULATES, NO RETURN)


-- ENUMS, EXTENSIONS
CREATE EXTENSION citext;
CREATE TYPE eventType AS ENUM ('lecture', 'seminar', 'conference', 'workshop', 'other');

------------------
 --   SCHEMA   --
------------------

-- CREATE TABLE participant(
--     participantID   SERIAL,
--     address         INET,
--     fName           VARCHAR(35),
--     lName           VARCHAR(35),
--     sysBan          BOOLEAN
--         DEFAULT 0,
-- );

CREATE TABLE host(
    hostID          SERIAL,
    address         INET,
    eAddress        citext UNIQUE,
    fName           VARCHAR(35),
    lName           VARCHAR(35),
    sysBan          BOOLEAN
        DEFAULT 0,
    PRIMARY KEY (hostID)
);

CREATE TABLE event(
    eventID     SERIAL,
    title       VARCHAR(32) NOT NULL,
    desc        VARCHAR(128) NOT NULL,
    type        eventType NOT NULL,
    eventCode   VARCHAR(4)
        CHECK (eventCode ~* '^[A-Z0-9]+$'), -- simulate upper alphanumeric
    hostID      SERIAL,
    -- FOREIGN KEY (hostID)
    --     REFERNCES host(hostID)
    --     ON UPDATE CASCADE
    --     ON DELETE CASCADE,
    PRIMARY KEY (eventID)
);
COMMENT ON TABLE event IS 'storage of an event';