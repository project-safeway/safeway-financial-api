package com.safeway.financial.infrastructure.persistence.specifications;

import com.safeway.financial.domain.specifications.PagamentoSpecification;
import com.safeway.financial.infrastructure.persistence.entities.PagamentoEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PagamentoJpaSpecification {

    public static Specification<PagamentoEntity> fromDomain(PagamentoSpecification domainSpec) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (domainSpec.getUsuarioId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("usuarioId"),
                        domainSpec.getUsuarioId()
                ));
            }

            if (domainSpec.getDataInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("dataPagamento"),
                        domainSpec.getDataInicio()
                ));
            }

            if (domainSpec.getDataFim() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("dataPagamento"),
                        domainSpec.getDataFim()
                ));
            }

            if (domainSpec.getValorMinimo() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("valorPagamento"),
                        domainSpec.getValorMinimo()
                ));
            }

            if (domainSpec.getValorMaximo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("valorPagamento"),
                        domainSpec.getValorMaximo()
                ));
            }

            if (domainSpec.getDescricao() != null && !domainSpec.getDescricao().isEmpty()) {
                String sanitized = domainSpec.getDescricao()
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_")
                        .toLowerCase();

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("descricao")),
                        "%" + sanitized + "%",
                        '\\'
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
