CREATE TABLE Course (
name		TEXT	PRIMARY KEY NOT NULL,
instructor	TEXT	NOT NULL,
capacity	INT		NOT NULL,
number		TEXT	NOT NULL,
departmentId INT    FOREIGN KEY (FK_departmentId) REFERENCES Department(departmentId)
)