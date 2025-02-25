package dev.MuragliaFood.Beer.dto;

import java.time.LocalDateTime;
import java.util.Arrays;

public class OrdineDTO {
	
	private Double totale;
	private ProdottoFront[] prodotti;
	private String username;
	private LocalDateTime data;

	public OrdineDTO(Double totale, ProdottoFront[] prodotti, String username, LocalDateTime data) {
		super();
		this.totale = totale;
		this.prodotti = prodotti;
		this.username = username;
		this.data = data;
	}
	
	public OrdineDTO() {
		super();
		this.totale = 0.0;
		this.prodotti = new ProdottoFront[0];
		this.username = "";
		this.data = null;
	}

	public Double getTotale() {
		return totale;
	}

	public void setTotale(Double totale) {
		this.totale = totale;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public ProdottoFront[] getProdotti() {
		return prodotti;
	}

	public void setProdotti(ProdottoFront[] prodotti) {
		this.prodotti = prodotti;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "OrdineDTO [totale=" + totale + ", prodotti=" + Arrays.toString(prodotti) + ", username=" + username
				+ ", data=" + data + "]";
	}
	
}
