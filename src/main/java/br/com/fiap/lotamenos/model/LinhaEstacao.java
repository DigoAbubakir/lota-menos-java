package br.com.fiap.lotamenos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "T_LM_LINHA_ESTACAO", uniqueConstraints = {
        @UniqueConstraint(name = "UK_LM_LE_LINHA_ESTACAO", columnNames = {"ID_LINHA", "ID_ESTACAO"}),
        @UniqueConstraint(name = "UK_LM_LE_LINHA_ORDEM", columnNames = {"ID_LINHA", "NR_ORDEM"})
})
public class LinhaEstacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LINHA_ESTACAO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LINHA", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_LE_LINHA"))
    private Linha linha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTACAO", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_LE_ESTACAO"))
    private Estacao estacao;

    @Column(name = "NR_ORDEM", nullable = false)
    private Integer ordem;

    public LinhaEstacao() {
    }

    public LinhaEstacao(Linha linha, Estacao estacao, Integer ordem) {
        this.linha = linha;
        this.estacao = estacao;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Linha getLinha() {
        return linha;
    }

    public void setLinha(Linha linha) {
        this.linha = linha;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public void setEstacao(Estacao estacao) {
        this.estacao = estacao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
