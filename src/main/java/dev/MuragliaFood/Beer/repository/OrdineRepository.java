package dev.MuragliaFood.Beer.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.MuragliaFood.Beer.model.Ordine;

@Repository
public interface OrdineRepository extends CrudRepository<Ordine, Integer> {
	Ordine findOrdineById(Integer id);
	List<Ordine> findAllByPaymentIdIsNotNull();
}
