package dev.MuragliaFood.Beer.dto;

import dev.MuragliaFood.Beer.util.TipoProdotto;

public class ProdottoDTO {
	private String nome;
	private String descrizione;
	private Double prezzo;
	private TipoProdotto tipo;
	
	public ProdottoDTO(String nome, String descrizione, Double prezzo, TipoProdotto tipo) {
		super();
		this.nome = nome;
		this.descrizione = descrizione;
		this.prezzo = prezzo;
		this.tipo = tipo;
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
	
}
