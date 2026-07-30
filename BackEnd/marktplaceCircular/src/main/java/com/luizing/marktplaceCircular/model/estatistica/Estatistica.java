package com.luizing.marktplaceCircular.model.estatistica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estatisticas")
@Getter
@Setter
@NoArgsConstructor
public class Estatistica {

    public static final Long ID_PRINCIPAL = 1L;

    @Id
    private Long id = ID_PRINCIPAL;

    @Column(nullable = false)
    private long totalAnunciosCriados;
}
