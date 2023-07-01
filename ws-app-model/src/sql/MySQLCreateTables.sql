-- ----------------------------------------------------------------------------
-- Model
-------------------------------------------------------------------------------
DROP TABLE Answer;
DROP TABLE Event;

----------------------------------- Event ------------------------------------
CREATE TABLE Event ( eventId BIGINT NOT NULL AUTO_INCREMENT,
    eventName VARCHAR(255) COLLATE latin1_bin NOT NULL,
    description VARCHAR(1024) COLLATE latin1_bin NOT NULL,
    duration SMALLINT NOT NULL,
    eventDate DATETIME NOT NULL,
    eventCreationDate DATETIME NOT NULL,
    cancellation BOOLEAN DEFAULT false,
    assistant SMALLINT DEFAULT 0,
    absent SMALLINT DEFAULT 0,
    CONSTRAINT EventPK PRIMARY KEY(eventId) ) ENGINE = InnoDB;

----------------------------------- Answer ------------------------------------

CREATE TABLE Answer ( answerId BIGINT NOT NULL AUTO_INCREMENT,
    eventId BIGINT NOT NULL,
    email VARCHAR(40) COLLATE latin1_bin NOT NULL,
    assistance BOOLEAN NOT NULL,
    answerDate DATETIME NOT NULL,
    CONSTRAINT AnswerPK PRIMARY KEY(answerId),
    CONSTRAINT AnswerEventFK FOREIGN KEY(eventId)
        REFERENCES Event(eventId) ON DELETE CASCADE ) ENGINE = InnoDB;
