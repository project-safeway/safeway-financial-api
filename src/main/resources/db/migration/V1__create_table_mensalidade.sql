CREATE TABLE mensalidades (
    id CHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fk_aluno CHAR(36) NOT NULL,
    data_vencimento DATE NOT NULL,
    valor_mensalidade DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDENTE', 'PAGO', 'ATRASADO', 'CANCELADO') NOT NULL,
    data_pagamento DATE,
    valor_pago DECIMAL(10, 2)
);