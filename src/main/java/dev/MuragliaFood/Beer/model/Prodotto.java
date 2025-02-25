package dev.MuragliaFood.Beer.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import dev.MuragliaFood.Beer.util.TipoProdotto;

@Entity
@Table (name="prodotto")
public class Prodotto {
	@Id
	@Column (name="idprodotto")
	@GeneratedValue (strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column (name = "nome", nullable = false)
	private String nome;
	@Column (name = "descrizione", nullable = false)
	private String descrizione;
	@Column (name = "prezzo", nullable = false)
	private Double prezzo;
	@Column (name = "tipo", nullable = false)
	@Enumerated (EnumType.STRING)
	private TipoProdotto tipo;
	@Column (name = "foto", nullable = false)
	private String foto;
	@ManyToMany (mappedBy = "prodotti")
	private List<Ordine> ordini; // Sostituire con lista id ordini?
	
	public Prodotto(Integer id, String nome, String descrizione, Double prezzo, TipoProdotto tipo, String foto) {
		super();
		this.id = id;
		this.nome = nome;
		this.descrizione = descrizione;
		this.prezzo = prezzo;
		this.tipo = tipo;
		this.foto = foto;
	}
	
	public Prodotto(Integer id, String nome, String descrizione, Double prezzo, TipoProdotto tipo) {
		super();
		this.id = id;
		this.nome = nome;
		this.descrizione = descrizione;
		this.prezzo = prezzo;
		this.tipo = tipo;
	}

	public Prodotto(String nome, String descrizione, Double prezzo, TipoProdotto tipo, String foto) {
		super();
		this.nome = nome;
		this.descrizione = descrizione;
		this.prezzo = prezzo;
		this.tipo = tipo;
		this.foto = foto;
	}
	
	public Prodotto(String nome, String descrizione, Double prezzo, TipoProdotto tipo) {
		super();
		this.nome = nome;
		this.descrizione = descrizione;
		this.prezzo = prezzo;
		this.tipo = tipo;
	}

	public Prodotto() {
		this.id = 0;
		this.nome = "";
		this.descrizione = "";
		this.prezzo = 0.0;
		this.tipo = null;
		this.foto = "";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Double prezzo) {
		this.prezzo = prezzo;
	}

	public TipoProdotto getTipo() {
		return tipo;
	}

	public void setTipo(TipoProdotto tipo) {
		this.tipo = tipo;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prodotto other = (Prodotto) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Prodotto [id=" + id + ", nome=" + nome + ", descrizione=" + descrizione + ", prezzo=" + prezzo
				+ ", tipo=" + tipo + ", foto=" + foto + ", ordini=" + ordini + "]";
	}
	
}

