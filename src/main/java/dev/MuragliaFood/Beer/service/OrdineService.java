package dev.MuragliaFood.Beer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.MuragliaFood.Beer.dao.OrdineDAO;
import dev.MuragliaFood.Beer.model.Ordine;
import dev.MuragliaFood.Beer.repository.OrdineRepository;

@Service
public class OrdineService implements OrdineDAO {
	
	private final OrdineRepository repo;
	
	public OrdineService(OrdineRepository repo) {
		this.repo = repo;
	}

	@Override
	public Ordine salvaOrdine(Ordine o) {
		return repo.save(o);
	}

	@Override
	public void eliminaOrdine(Integer id) {
		repo.deleteById(id);
	}

	@Override
	public Ordine recuperaOrdine(Integer id) {
		return repo.findOrdineById(id);
	}

	@Override
	public List<Ordine> recuperaTuttiOrdini() {
		return (List<Ordine>)repo.findAll();
	}

	@Override
	public List<Ordine> recuperaOrdiniConPayment() {
		return repo.findAllByPaymentIdIsNotNull();
	}

}
