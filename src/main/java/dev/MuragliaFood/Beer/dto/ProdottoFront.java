package dev.MuragliaFood.Beer.dto;

import java.util.Objects;

import dev.MuragliaFood.Beer.util.TipoProdotto;

public class ProdottoFront {

	private int id;
	private String nome;
	private String descrizione;
	private Double prezzo;
	private TipoProdotto tipo;
	private String foto;
	private int quantity;
	
	public ProdottoFront(int id, String nome, String descrizione, Double prezzo, TipoProdotto tipo, String foto, int quantity) {
		this.id = id;
		this.nome = nome;
		this.descrizione = descrizione;
		this.prezzo = prezzo;
		this.tipo = tipo;
		this.foto = foto;
		this.quantity = quantity;
	}
	
	public ProdottoFront() {
		this.id = 0;
		this.nome = "";
		this.descrizione = "";
		this.prezzo = 0.0;
		this.tipo = null;
		this.foto = null;
		this.quantity = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(descrizione, foto, id, nome, prezzo, quantity, tipo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProdottoFront other = (ProdottoFront) obj;
		return Objects.equals(descrizione, other.descrizione) && Objects.equals(foto, other.foto) && id == other.id
				&& Objects.equals(nome, other.nome) && Objects.equals(prezzo, other.prezzo)
				&& quantity == other.quantity && tipo == other.tipo;
	}

	@Override
	public String toString() {
		return "ProdottoFront [id=" + id + ", nome=" + nome + ", descrizione=" + descrizione + ", prezzo=" + prezzo
				+ ", tipo=" + tipo + ", foto=" + foto + ", quantity=" + quantity + "]";
	}
	
}
