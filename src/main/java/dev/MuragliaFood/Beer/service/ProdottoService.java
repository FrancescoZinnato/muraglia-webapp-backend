package dev.MuragliaFood.Beer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.MuragliaFood.Beer.dao.ProdottoDAO;
import dev.MuragliaFood.Beer.model.Prodotto;
import dev.MuragliaFood.Beer.repository.ProdottoRepository;
import dev.MuragliaFood.Beer.util.TipoProdotto;

@Service
public class ProdottoService implements ProdottoDAO {
	
	private final ProdottoRepository repo;
	
	public ProdottoService(ProdottoRepository repo) {
		this.repo = repo;
	}

	@Override
	public Prodotto salvaProdotto(Prodotto p) {
		return repo.save(p);
	}

	@Override
	public void eliminaProdotto(Integer id) {
		repo.deleteById(id);
	}

	@Override
	public Prodotto recuperaProdotto(Integer id) {
		return repo.findProdottoById(id);
	}

	@Override
	public List<Prodotto> recuperaProdotti() {
		return (List<Prodotto>)repo.findAll();
	}

	@Override
	public List<Prodotto> recuperaProdottiTipo(TipoProdotto tipo) {
		return repo.findProdottiByTipo(tipo);
	}

}
