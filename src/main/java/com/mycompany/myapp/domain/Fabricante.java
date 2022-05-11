package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Fabricante.
 */
@Entity
@Table(name = "fabricante")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Fabricante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @OneToMany(mappedBy = "fabricante")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "fabricante" }, allowSetters = true)
    private Set<Produto> produtos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Fabricante id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Fabricante nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Produto> getProdutos() {
        return this.produtos;
    }

    public void setProdutos(Set<Produto> produtos) {
        if (this.produtos != null) {
            this.produtos.forEach(i -> i.setFabricante(null));
        }
        if (produtos != null) {
            produtos.forEach(i -> i.setFabricante(this));
        }
        this.produtos = produtos;
    }

    public Fabricante produtos(Set<Produto> produtos) {
        this.setProdutos(produtos);
        return this;
    }

    public Fabricante addProdutos(Produto produto) {
        this.produtos.add(produto);
        produto.setFabricante(this);
        return this;
    }

    public Fabricante removeProdutos(Produto produto) {
        this.produtos.remove(produto);
        produto.setFabricante(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Fabricante)) {
            return false;
        }
        return id != null && id.equals(((Fabricante) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Fabricante{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            "}";
    }
}
