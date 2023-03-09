CREATE TABLE utilisateurs(
    id INTEGER PRIMARY KEY,
    nickname VARCHAR(70) NOT NULL,
    dateCreation date NOT NULL,
    nbAmis INTEGER NOT NULL,
    etoilesMoyennes DOUBLE PRECISION NOT NULL,
    nbCompliments INTEGER NOT NULL,
    nbFollowers INTEGER NOT NULL,
    nbReactions INTEGER NOT NULL,
    nbAnneeElite INTEGER NOT NULL
);