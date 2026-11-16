package br.com.fiap.lotamenos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "T_LM_ESTACAO",
        uniqueConstraints = @UniqueConstraint(name = "UK_LM_ESTACAO_NOME", columnNames = "NM_ESTACAO"))
public class Estacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTACAO")
    private Long id;

    @Column(name = "NM_ESTACAO", nullable = false, length = 60)
    private String nome;

    @Column(name = "ST_INTEGRACAO", nullable = false)
    private Boolean possuiIntegracao;

    @Column(name = "ST_ATIVA", nullable = false)
    private Boolean ativa;

    public Estacao() {
    }

    public Estacao(String nome, Boolean possuiIntegracao, Boolean ativa) {
        this.nome = nome;
        this.possuiIntegracao = possuiIntegracao;
        this.ativa = ativa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getPossuiIntegracao() {
        return possuiIntegracao;
    }

    public void setPossuiIntegracao(Boolean possuiIntegracao) {
        this.possuiIntegracao = possuiIntegracao;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
