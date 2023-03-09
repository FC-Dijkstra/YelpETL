CREATE TABLE localisation (
    id INTEGER PRIMARY KEY,
    adresse VARCHAR(200) NOT NULL,
    codePostal VARCHAR(20) NOT NULL,
    ville VARCHAR(50) NOT NULL,
    etat VARCHAR(70) NOT NULL
);
