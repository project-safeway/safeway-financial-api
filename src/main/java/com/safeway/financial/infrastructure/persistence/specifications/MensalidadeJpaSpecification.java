package com.safeway.financial.infrastructure.persistence.specifications;

import com.safeway.financial.domain.specifications.MensalidadeSpecification;
import com.safeway.financial.infrastructure.persistence.entities.MensalidadeEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MensalidadeJpaSpecification {

    public static Specification<MensalidadeEntity> fromDomain(MensalidadeSpecification domainSpec){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (domainSpec.getAlunoId() != null){
                predicates.add(criteriaBuilder.equal(
                        root.get("alunoId"),
                        domainSpec.getAlunoId()
                ));
            }

            if (domainSpec.getDataInicio() != null){
                predicates.add(criteriaBuilder.equal(
                        root.get("dataInicio"),
                        domainSpec.getDataInicio()
                ));
            }

            if (domainSpec.getDataFim() != null){
                predicates.add(criteriaBuilder.equal(
                        root.get("dataFim"),
                        domainSpec.getDataFim()
                ));
            }

            if (domainSpec.getStatus() != null && !domainSpec.getStatus().isEmpty()){
                predicates.add(root.get("status").in(domainSpec.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
