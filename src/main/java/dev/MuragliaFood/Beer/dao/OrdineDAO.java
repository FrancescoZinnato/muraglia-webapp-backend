package dev.MuragliaFood.Beer.dao;

import java.util.List;

import dev.MuragliaFood.Beer.model.Ordine;

public interface OrdineDAO {
	
	Ordine salvaOrdine(Ordine o);
	void eliminaOrdine(Integer id);
	Ordine recuperaOrdine(Integer id);
	List<Ordine> recuperaTuttiOrdini();
	List<Ordine> recuperaOrdiniConPayment();

}
