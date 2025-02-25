package dev.MuragliaFood.Beer.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.MuragliaFood.Beer.model.Prodotto;
import dev.MuragliaFood.Beer.service.ImageService;
import dev.MuragliaFood.Beer.service.ProdottoService;
import dev.MuragliaFood.Beer.util.TipoProdotto;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/prodotti")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProdottiController {
	
	private final ProdottoService service;
	private final ImageService imgService;
	private final String fotoProdottiDirectory = "src/main/resources/static/fotoProdotti";
	private static final Logger logger = LoggerFactory.getLogger(ProdottiController.class);
	
	public ProdottiController(ProdottoService service, ImageService imgService) {
		this.service = service;
		this.imgService = imgService;
	}
	
	@GetMapping("/recuperaProdotti")
	public List<Prodotto> recuperaProdotti(){
		List<Prodotto> prodotti = service.recuperaProdotti();
		return prodotti;
	}
	
	@GetMapping("/recuperaProdotti/{tipo}")
	public List<Prodotto> recuperaProdottiTipo(@PathVariable TipoProdotto tipo){
		List<Prodotto> prodotti = service.recuperaProdottiTipo(tipo);
		return prodotti;
	}
	
	@GetMapping("/recuperaProdotto/{id}")
	public Prodotto recuperaProdotto(@PathVariable Integer id) {
		Prodotto p = service.recuperaProdotto(id);
		return p;
	}
	
	@PostMapping("/admin/aggiungiProdotto")
	public Prodotto aggiungiProdotto(@RequestParam("prodotto") String prodotto, @RequestParam("foto") MultipartFile foto) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Prodotto p = mapper.readValue(prodotto, Prodotto.class);
		//Salvare il file immagine nella directory ed assegnare il path al prodotto
		String fotoNome = imgService.saveImageToStorage(fotoProdottiDirectory, foto);
		p.setFoto(fotoNome);
		Prodotto prodSalvato = service.salvaProdotto(p);
		return prodSalvato;
	}
	
	@PostMapping("/admin/modificaProdotto")
	//Dovrei usare @RequestPart perchè teoricamente la richiesta dovrebbe essere di content-type multipart/form-data
	//Ma in realtà il frontend me la manda come application/json (non so perchè) quindi uso @RequestParam
	public Prodotto modificaProdotto(@RequestParam("prodotto") String prodotto, @RequestParam(value = "foto", required = false) MultipartFile foto) throws IOException {
		System.out.println(prodotto);
		ObjectMapper mapper = new ObjectMapper();
		Prodotto p = mapper.readValue(prodotto, Prodotto.class);
		if(p.getFoto() != null && foto != null) {
			imgService.deleteImage(fotoProdottiDirectory, p.getFoto());
			p.setFoto(null);
		}
		//Salvare il file immagine nella directory ed assegnare il path al prodotto
		if(foto != null) {
			String fotoNome = imgService.saveImageToStorage(fotoProdottiDirectory, foto);
			p.setFoto(fotoNome);
		}
		Prodotto prodModificato = service.salvaProdotto(p);
		return prodModificato;
	}
	
	@DeleteMapping("/admin/eliminaProdotto/{id}")
	public ResponseEntity<?> eliminaProdotto(@PathVariable Integer id) throws IOException{
		Prodotto p = service.recuperaProdotto(id);
		if(imgService.deleteImage(fotoProdottiDirectory, p.getFoto())) {
			service.eliminaProdotto(id);
			return ResponseEntity.ok(Collections.singletonMap("eliminato", true));
		}
		if(service.recuperaProdotto(id) != null) {
			logger.info("Prodotto con id: " + id + " non veramente eliminato");
			return ResponseEntity.ok(Collections.singletonMap("eliminato", false));
		}
		return ResponseEntity.ok(Collections.singletonMap("eliminato", false));
	}
	
}
