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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Consulta de planejamento de viagem e a sugestão de horário dada pela API
 * (tabela T_LM_CONSULTA_VIAGEM). Base do indicador de impacto.
 */
@Entity
@Table(name = "T_LM_CONSULTA_VIAGEM")
public class ConsultaViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONSULTA")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_CON_USUARIO"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTACAO_ORIGEM", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_CON_ORIGEM"))
    private Estacao origem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTACAO_DESTINO", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_CON_DESTINO"))
    private Estacao destino;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_SENTIDO", nullable = false, length = 20)
    private Sentido sentido;

    /** Data da viagem planejada. */
    @Column(name = "DT_VIAGEM", nullable = false)
    private LocalDate dataViagem;

    /** "HH:mm" */
    @Column(name = "HR_DESEJADO", nullable = false, length = 5)
    private String horarioDesejado;

    /** "HH:mm" */
    @Column(name = "HR_SUGERIDO", nullable = false, length = 5)
    private String horarioSugerido;

    @Column(name = "NR_INDICE_DESEJADO", nullable = false)
    private Integer indiceDesejado;

    @Column(name = "NR_INDICE_SUGERIDO", nullable = false)
    private Integer indiceSugerido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTACAO_CRITICA", foreignKey = @ForeignKey(name = "FK_LM_CON_CRITICA"))
    private Estacao estacaoCritica;

    /** true = aceitou, false = recusou, null = ainda não respondeu. */
    @Column(name = "ST_SUGESTAO_ACEITA")
    private Boolean sugestaoAceita;

    @Column(name = "DT_CONSULTA", nullable = false)
    private LocalDateTime dataConsulta;

    public ConsultaViagem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Estacao getOrigem() {
        return origem;
    }

    public void setOrigem(Estacao origem) {
        this.origem = origem;
    }

    public Estacao getDestino() {
        return destino;
    }

    public void setDestino(Estacao destino) {
        this.destino = destino;
    }

    public Sentido getSentido() {
        return sentido;
    }

    public void setSentido(Sentido sentido) {
        this.sentido = sentido;
    }

    public LocalDate getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(LocalDate dataViagem) {
        this.dataViagem = dataViagem;
    }

    public String getHorarioDesejado() {
        return horarioDesejado;
    }

    public void setHorarioDesejado(String horarioDesejado) {
        this.horarioDesejado = horarioDesejado;
    }

    public String getHorarioSugerido() {
        return horarioSugerido;
    }

    public void setHorarioSugerido(String horarioSugerido) {
        this.horarioSugerido = horarioSugerido;
    }

    public Integer getIndiceDesejado() {
        return indiceDesejado;
    }

    public void setIndiceDesejado(Integer indiceDesejado) {
        this.indiceDesejado = indiceDesejado;
    }

    public Integer getIndiceSugerido() {
        return indiceSugerido;
    }

    public void setIndiceSugerido(Integer indiceSugerido) {
        this.indiceSugerido = indiceSugerido;
    }

    public Estacao getEstacaoCritica() {
        return estacaoCritica;
    }

    public void setEstacaoCritica(Estacao estacaoCritica) {
        this.estacaoCritica = estacaoCritica;
    }

    public Boolean getSugestaoAceita() {
        return sugestaoAceita;
    }

    public void setSugestaoAceita(Boolean sugestaoAceita) {
        this.sugestaoAceita = sugestaoAceita;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }
}
