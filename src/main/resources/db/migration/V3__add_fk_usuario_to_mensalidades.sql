ALTER TABLE mensalidades
    ADD COLUMN fk_usuario CHAR(36) NULL;

CREATE INDEX idx_mensalidades_fk_usuario ON mensalidades(fk_usuario);
CREATE INDEX idx_mensalidades_fk_usuario_data_vencimento ON mensalidades(fk_usuario, data_vencimento);
