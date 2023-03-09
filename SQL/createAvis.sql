CREATE TABLE avis(
    id INTEGER PRIMARY KEY,
    idUtilisateur INTEGER NOT NULL,
    idCommerce INTEGER NOT NULL,
    idDate INTEGER NOT NULL,
    note DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_avis_utilisateur FOREIGN KEY (idUtilisateur) REFERENCES utilisateurs(id),
    CONSTRAINT fk_avis_commerce FOREIGN KEY (idCommerce) REFERENCES commerces(id),
    CONSTRAINT fk_avis_temporalite FOREIGN KEY (idDate) REFERENCES temporalite(id)
);