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

@Entity
@Table(name = "T_LM_LINHA",
        uniqueConstraints = @UniqueConstraint(name = "UK_LM_LINHA_NUMERO", columnNames = "NR_LINHA"))
public class Linha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LINHA")
    private Long id;

    @Column(name = "NR_LINHA", nullable = false)
    private Integer numero;

    @Column(name = "NM_LINHA", nullable = false, length = 40)
    private String nome;

    @Column(name = "DS_COR", nullable = false, length = 7)
    private String cor;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_OPERADORA", nullable = false, length = 10)
    private Operadora operadora;

    public Linha() {
    }

    public Linha(Integer numero, String nome, String cor, Operadora operadora) {
        this.numero = numero;
        this.nome = nome;
        this.cor = cor;
        this.operadora = operadora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Operadora getOperadora() {
        return operadora;
    }

    public void setOperadora(Operadora operadora) {
        this.operadora = operadora;
    }
}
