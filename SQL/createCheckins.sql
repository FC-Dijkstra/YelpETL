CREATE TABLE checkins(
    id INTEGER PRIMARY KEY,
    idCommerce INTEGER NOT NULL,
    idDate INTEGER NOT NULL,
    CONSTRAINT fk_checkins_commerce FOREIGN KEY (idCommerce) REFERENCES commerces(id),
    CONSTRAINT fk_checkins_temporalite FOREIGN KEY (idDate) REFERENCES temporalite(id)
);