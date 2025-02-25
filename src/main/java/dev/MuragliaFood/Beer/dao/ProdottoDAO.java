package dev.MuragliaFood.Beer.dao;

import java.util.List;

import dev.MuragliaFood.Beer.model.Prodotto;
import dev.MuragliaFood.Beer.util.TipoProdotto;

public interface ProdottoDAO {
	
	Prodotto salvaProdotto(Prodotto p);
	void eliminaProdotto(Integer id);
	Prodotto recuperaProdotto(Integer id);
	List<Prodotto> recuperaProdotti();
	List<Prodotto> recuperaProdottiTipo(TipoProdotto tipo);
	
}
