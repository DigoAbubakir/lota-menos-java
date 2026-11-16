package br.com.fiap.lotamenos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Índice histórico de lotação por estação, linha, sentido, tipo de dia e faixa de 15 minutos
 * (tabela T_LM_HISTORICO_LOTACAO).
 */
@Entity
@Table(name = "T_LM_HISTORICO_LOTACAO", uniqueConstraints = @UniqueConstraint(
        name = "UK_LM_HIST_CHAVE",
        columnNames = {"ID_ESTACAO", "ID_LINHA", "DS_SENTIDO", "DS_TIPO_DIA", "HR_FAIXA"}))
public class HistoricoLotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HISTORICO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTACAO", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_HIST_ESTACAO"))
    private Estacao estacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LINHA", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_HIST_LINHA"))
    private Linha linha;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_SENTIDO", nullable = false, length = 20)
    private Sentido sentido;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_TIPO_DIA", nullable = false, length = 10)
    private TipoDia tipoDia;

    /** Início da faixa de 15 minutos no formato "HH:mm" (ex.: "07:15"). */
    @Column(name = "HR_FAIXA", nullable = false, length = 5)
    private String faixaHorario;

    @Column(name = "NR_INDICE", nullable = false)
    private Integer indice;

    public HistoricoLotacao() {
    }

    public HistoricoLotacao(Estacao estacao, Linha linha, Sentido sentido, TipoDia tipoDia,
                            String faixaHorario, Integer indice) {
        this.estacao = estacao;
        this.linha = linha;
        this.sentido = sentido;
        this.tipoDia = tipoDia;
        this.faixaHorario = faixaHorario;
        this.indice = indice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public void setEstacao(Estacao estacao) {
        this.estacao = estacao;
    }

    public Linha getLinha() {
        return linha;
    }

    public void setLinha(Linha linha) {
        this.linha = linha;
    }

    public Sentido getSentido() {
        return sentido;
    }

    public void setSentido(Sentido sentido) {
        this.sentido = sentido;
    }

    public TipoDia getTipoDia() {
        return tipoDia;
    }

    public void setTipoDia(TipoDia tipoDia) {
        this.tipoDia = tipoDia;
    }

    public String getFaixaHorario() {
        return faixaHorario;
    }

    public void setFaixaHorario(String faixaHorario) {
        this.faixaHorario = faixaHorario;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }
}
