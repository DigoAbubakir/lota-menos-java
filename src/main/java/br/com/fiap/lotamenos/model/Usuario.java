package br.com.fiap.lotamenos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_LM_USUARIO",
        uniqueConstraints = @UniqueConstraint(name = "UK_LM_USUARIO_EMAIL", columnNames = "DS_EMAIL"))
public class Usuario {
    public static final double REPUTACAO_INICIAL = 1.0;
    public static final double REPUTACAO_MINIMA = 0.3;
    public static final double REPUTACAO_MAXIMA = 1.5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "NM_USUARIO", nullable = false, length = 100)
    private String nome;

    @Column(name = "DS_EMAIL", nullable = false, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_PERFIL", nullable = false, length = 25)
    private PerfilUsuario perfil;

    @Column(name = "VL_REPUTACAO", nullable = false)
    private Double reputacao = REPUTACAO_INICIAL;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    public Usuario() {
    }

    public Usuario(String nome, String email, PerfilUsuario perfil, Double reputacao, LocalDateTime dataCadastro) {
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.reputacao = reputacao;
        this.dataCadastro = dataCadastro;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public Double getReputacao() {
        return reputacao;
    }

    public void setReputacao(Double reputacao) {
        this.reputacao = reputacao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
