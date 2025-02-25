package dev.MuragliaFood.Beer.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import dev.MuragliaFood.Beer.util.StatoOrdine;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (name = "ordine")
public class Ordine {
	@Id
	@Column (name = "idordine")
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column (name = "payment_id")
	private String paymentId;
	@Column (name = "totale", nullable = false)
	private Double totale;
	@Column (name = "data", nullable = false)
	private LocalDateTime data;
	@Enumerated (EnumType.STRING)
	private StatoOrdine stato;
	@ManyToOne
	@JoinColumn (name = "utente_id")
	private User utente; // Sostituire con id utente?
	@ManyToMany
	@JoinTable (name = "ordine_prodotto", joinColumns = @JoinColumn (name = "ordine_id"), inverseJoinColumns = @JoinColumn (name = "prodotto_id"))
	private List<Prodotto> prodotti; // Sostituire con lista id prodotti?
	
	public Ordine(Integer id, String paymentId, Double totale, LocalDateTime data, StatoOrdine stato, User utente, List<Prodotto> prodotti) {
		this.id = id;
		this.paymentId = paymentId;
		this.totale = totale;
		this.data = data;
		this.stato = stato;
		this.utente = utente;
		this.prodotti = prodotti;
	}

	public Ordine(Double totale, LocalDateTime data, StatoOrdine stato, User utente, List<Prodotto> prodotti) {
		this.totale = totale;
		this.data = data;
		this.stato = stato;
		this.utente = utente;
		this.prodotti = prodotti;
	}

	public Ordine() {
		this.id = 0;
		this.totale = 0.0;
		this.data = null;
		this.stato = null;
		this.utente = null;
		this.prodotti = null;
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

	public User getUtente() {
		return utente;
	}

	public void setUtente(User utente) {
		this.utente = utente;
	}

	public List<Prodotto> getProdotti() {
		return prodotti;
	}

	public void setProdotti(List<Prodotto> prodotti) {
		this.prodotti = prodotti;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, id, paymentId, prodotti, stato, totale, utente);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ordine other = (Ordine) obj;
		return Objects.equals(data, other.data) && Objects.equals(id, other.id)
				&& Objects.equals(paymentId, other.paymentId) && Objects.equals(prodotti, other.prodotti)
				&& stato == other.stato && Objects.equals(totale, other.totale) && Objects.equals(utente, other.utente);
	}

	@Override
	public String toString() {
		return "Ordine [id=" + id + ", payment_id=" + paymentId + ", totale=" + totale + ", data=" + data + ", stato="
				+ stato + ", utente=" + utente + ", prodotti=" + prodotti + "]";
	}
	
}
