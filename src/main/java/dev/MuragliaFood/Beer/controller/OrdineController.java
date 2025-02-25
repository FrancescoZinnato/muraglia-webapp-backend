package dev.MuragliaFood.Beer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.MuragliaFood.Beer.dto.OrdineFront;
import dev.MuragliaFood.Beer.model.Ordine;
import dev.MuragliaFood.Beer.service.OrdineService;

@RestController
@RequestMapping("/ord")
public class OrdineController {
	
	private final OrdineService ordService;
	
	public OrdineController(OrdineService ordService) {
		this.ordService = ordService;
	}
	
	@GetMapping("/getOrdine")
	public OrdineFront recuperaOrdine() {
		Ordine o = ordService.recuperaOrdine(17);
		OrdineFront or = new OrdineFront(o.getId(), o.getTotale(), o.getData(), o.getStato(), o.getProdotti(), o.getUtente().getId());
		return or;
	}
	
	@GetMapping("admin/getOrdini")
	public List<OrdineFront> recuperaOrdini(){
		List<Ordine> o = this.ordService.recuperaTuttiOrdini();
		List<OrdineFront> or = new ArrayList<OrdineFront>();
		for(Ordine x : o) {
			OrdineFront n = new OrdineFront(x.getId(), x.getTotale(), x.getData(), x.getStato(), x.getProdotti(), x.getUtente().getId());
			or.add(n);
		}
		return or;
	}
	
	@GetMapping("admin/getOrdinipayment")
	public List<OrdineFront> recuperaOrdinipayment(){
		List<Ordine> o = this.ordService.recuperaOrdiniConPayment();
		List<OrdineFront> or = new ArrayList<OrdineFront>();
		for(Ordine x : o) {
			OrdineFront n = new OrdineFront(x.getId(), x.getTotale(), x.getData(), x.getStato(), x.getProdotti(), x.getUtente().getId());
			or.add(n);
		}
		return or;
	}
	
}
