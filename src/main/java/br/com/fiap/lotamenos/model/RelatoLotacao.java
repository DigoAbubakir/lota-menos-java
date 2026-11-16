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

import java.time.LocalDateTime;

/**
 * Relato de lotação em tempo real enviado por um passageiro (tabela T_LM_RELATO_LOTACAO).
 */
@Entity
@Table(name = "T_LM_RELATO_LOTACAO")
public class RelatoLotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RELATO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_REL_USUARIO"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTACAO", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_REL_ESTACAO"))
    private Estacao estacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LINHA", nullable = false, foreignKey = @ForeignKey(name = "FK_LM_REL_LINHA"))
    private Linha linha;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_SENTIDO", nullable = false, length = 20)
    private Sentido sentido;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_NIVEL", nullable = false, length = 10)
    private NivelLotacao nivel;

    @Column(name = "DT_RELATO", nullable = false)
    private LocalDateTime dataHora;

    public RelatoLotacao() {
    }

    public RelatoLotacao(Usuario usuario, Estacao estacao, Linha linha, Sentido sentido,
                         NivelLotacao nivel, LocalDateTime dataHora) {
        this.usuario = usuario;
        this.estacao = estacao;
        this.linha = linha;
        this.sentido = sentido;
        this.nivel = nivel;
        this.dataHora = dataHora;
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

    public NivelLotacao getNivel() {
        return nivel;
    }

    public void setNivel(NivelLotacao nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
