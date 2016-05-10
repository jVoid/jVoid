create table executions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    timestamp BIGINT NOT NULL
);

CREATE TABLE methods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    executionId BIGINT NOT NULL,
    identifier VARCHAR(2048) NOT NULL,
    checksum VARCHAR(1024) NOT NULL,
    FOREIGN KEY (executionId)  REFERENCES executions(id)
);
CREATE INDEX identifier_methods_idx ON methods(identifier);
CREATE INDEX identifier_executionId_methods_idx ON methods(identifier, executionId);

CREATE TABLE classes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    executionId BIGINT NOT NULL,
    identifier VARCHAR(2048) NOT NULL,
    checksum VARCHAR(1024) NOT NULL,
    superclassIdentifier VARCHAR(20148) NULL,
    FOREIGN KEY (executionId)  REFERENCES executions(id)
);
CREATE INDEX identifier_classes_idx ON classes(identifier);
CREATE INDEX identifier_classes_executionId_idx ON classes(identifier, executionId);

CREATE TABLE class_constructors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    identifier VARCHAR(2048) NOT NULL,
    checksum VARCHAR(1024) NOT NULL,
    classId BIGINT NULL,
    FOREIGN KEY (classId)  REFERENCES classes(id)
);
CREATE INDEX identifier_class_constructors_idx ON class_constructors(identifier);

CREATE TABLE class_static_blocks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    identifier VARCHAR(2048) NOT NULL,
    checksum VARCHAR(1024) NOT NULL,
    classId BIGINT NULL,
    FOREIGN KEY (classId)  REFERENCES classes(id)
);
CREATE INDEX identifier_class_static_blocks_idx ON class_static_blocks(identifier);

CREATE TABLE tests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    executionId BIGINT NOT NULL,
    identifier VARCHAR(2048) NOT NULL,
    runStatus VARCHAR(255) NOT NULL DEFAULT FALSE,
    FOREIGN KEY (executionId)  REFERENCES executions(id)
);
CREATE INDEX identifier_tests_idx ON tests(identifier);
CREATE INDEX identifier_executionId_tests_idx ON tests(identifier, executionId);

CREATE TABLE test_classes (
    testId BIGINT NOT NULL,
    classId BIGINT NOT NULL,
    PRIMARY KEY (testId, classId),
    FOREIGN KEY (testId)  REFERENCES tests(id),
    FOREIGN KEY (classId)  REFERENCES classes(id)
);

CREATE TABLE test_methods (
    testId BIGINT NOT NULL,
    methodId BIGINT NOT NULL,
    PRIMARY KEY (testId, methodId),
    FOREIGN KEY (testId)  REFERENCES tests(id),
    FOREIGN KEY (methodId)  REFERENCES methods(id)
);