package dev.MuragliaFood.Beer.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import dev.MuragliaFood.Beer.model.Prodotto;
import dev.MuragliaFood.Beer.util.StatoOrdine;

public class OrdineFront {

	private Integer id;
	private Double totale;
	private LocalDateTime data;
	private StatoOrdine stato;
	private List<Prodotto> prodotti;
	private Integer id_utente;
	
	public OrdineFront(Integer id, Double totale, LocalDateTime data, StatoOrdine stato, List<Prodotto> prodotti, Integer id_utente) {
		super();
		this.id = id;
		this.totale = totale;
		this.data = data;
		this.stato = stato;
		this.prodotti = prodotti;
		this.id_utente = id_utente;
	}
	
	public OrdineFront() {
		this.id = 0;
		this.totale = 0.0;
		this.data = null;
		this.stato = null;
		this.prodotti = null;
		this.id_utente = 0;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getTotale() {
		return totale;
	}

	public void setTotale(Double totale) {
		this.totale = totale;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public StatoOrdine getStato() {
		return stato;
	}

	public void setStato(StatoOrdine stato) {
		this.stato = stato;
	}

	public List<Prodotto> getProdotti() {
		return prodotti;
	}

	public void setProdotti(List<Prodotto> prodotti) {
		this.prodotti = prodotti;
	}

	public Integer getId_utente() {
		return id_utente;
	}

	public void setId_utente(Integer id_utente) {
		this.id_utente = id_utente;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, id, id_utente, prodotti, stato, totale);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrdineFront other = (OrdineFront) obj;
		return Objects.equals(data, other.data) && Objects.equals(id, other.id)
				&& Objects.equals(id_utente, other.id_utente) && Objects.equals(prodotti, other.prodotti)
				&& stato == other.stato && Objects.equals(totale, other.totale);
	}

	@Override
	public String toString() {
		return "OrdineFront [id=" + id + ", totale=" + totale + ", data=" + data + ", stato=" + stato + ", prodotti="
				+ prodotti + ", id_utente=" + id_utente + "]";
	}
	
}
