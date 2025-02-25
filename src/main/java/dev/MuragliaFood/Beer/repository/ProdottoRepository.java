package dev.MuragliaFood.Beer.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.MuragliaFood.Beer.model.Prodotto;
import dev.MuragliaFood.Beer.util.TipoProdotto;

@Repository
public interface ProdottoRepository extends CrudRepository<Prodotto, Integer> {
	Prodotto findProdottoById(Integer id);
	List<Prodotto> findProdottiByTipo(TipoProdotto tipo);
}
