DELETE FROM mensalidades;

ALTER TABLE mensalidades
    MODIFY COLUMN fk_usuario CHAR(36) NOT NULL;
