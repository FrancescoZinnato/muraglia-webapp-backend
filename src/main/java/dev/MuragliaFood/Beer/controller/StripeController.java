package dev.MuragliaFood.Beer.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import dev.MuragliaFood.Beer.dto.OrdineDTO;
import dev.MuragliaFood.Beer.dto.OrdineFront;
import dev.MuragliaFood.Beer.dto.ProdottoFront;
import dev.MuragliaFood.Beer.model.Ordine;
import dev.MuragliaFood.Beer.model.Prodotto;
import dev.MuragliaFood.Beer.model.User;
import dev.MuragliaFood.Beer.service.OrdineService;
import dev.MuragliaFood.Beer.service.ProdottoService;
import dev.MuragliaFood.Beer.service.UserService;
import dev.MuragliaFood.Beer.util.JwtUtils;
import dev.MuragliaFood.Beer.util.StatoOrdine;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/stripe")
public class StripeController {
	
	//@Value("${stripe.secret}")
	private final String secret;
	private final String stripePublic;
	private final String webhookSecret;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	private final UserService userService;
	private final ProdottoService prodService;
	private final OrdineService ordService;
	private final JwtUtils jwtUtils;
	
	public StripeController(@Value("${stripe.public}") String stripePublic, @Value("${stripe.secret}") String secret, UserService userService, @Value("${stripe.webhook.secret}") String webhookSecret, ProdottoService prodService, OrdineService ordService, JwtUtils jwtUtils) {
		this.stripePublic = stripePublic;
		this.secret = secret;
		this.userService = userService;
		this.webhookSecret = webhookSecret;
		this.prodService = prodService;
		this.ordService = ordService;
		this.jwtUtils = jwtUtils;
	}
	
	private String estraiTokenDaiCookie(Cookie[] cookies) {
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("token".equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}
	
	private String estraiUsernameDaiCookie(Cookie[] cookies) {
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("username".equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}
	
	@PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody OrdineDTO order, HttpServletRequest request) {
		final String token = this.estraiTokenDaiCookie(request.getCookies());
		User user = this.userService.findByUsername(jwtUtils.extractUsername(token));
		
		if(user == null) {
			System.out.println("L'utente è null");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<Prodotto> prodList = new ArrayList<Prodotto>();
		for(ProdottoFront p : order.getProdotti()) {
			for(int i = 0; i<p.getQuantity(); i++) {
				Prodotto pr = this.prodService.recuperaProdotto(p.getId());
				prodList.add(pr);
			}
		}
		
        try {
            // Imposta la chiave segreta di Stripe
            Stripe.apiKey = this.secret;

            // Crea i line items per ogni prodotto nell'ordine
            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            Double totale = 0.0;
            for (Prodotto prod : prodList) {
            	totale += prod.getPrezzo();
                lineItems.add(
                    SessionCreateParams.LineItem.builder()
                        //.setQuantity((long) prod.getQuantity())  // Usa la quantità del prodotto ordinato (vuole un long)
                    	.setQuantity(1L) // 1 di default perchè recupero i prodotti dal db quindi li ho tutti singolarmente
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount((long) (prod.getPrezzo() * 100))  // Prezzo in centesimi (vuole un long)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(prod.getNome())  // Nome del prodotto
                                        .build()
                                )
                                .build()
                        )
                        .build()
                );
            }
            
            // Creo l'ordine da salvare nel db e recupero l'id da allegare al checkout verso Stripe per poter collegare il pagamento al ricevimento del webhook
            Ordine o = new Ordine(totale, order.getData(), StatoOrdine.PENDING, user, prodList);
            
            String d = o.getData().format(formatter); // String rappresentante la data secondo il pattern del formatter
            o.setData(LocalDateTime.parse(d, formatter).plusHours(1)); // Aggiungo 1h per avere l'orario esatto della mia timezone
            
            Ordine ordineDB = this.ordService.salvaOrdine(o);

            // Crea i parametri della sessione
            SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)  // Abilita pagamenti con carta(GooglePay e ApplePay compresi?)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PAYPAL)
                .addAllLineItem(lineItems)  // Aggiungi tutti i line items
                .setMode(SessionCreateParams.Mode.PAYMENT)  // Imposta la modalità di pagamento
                .setPaymentIntentData( // Imposta la cattura manuale del pagamento
                			SessionCreateParams.PaymentIntentData.builder()
                				.setCaptureMethod(SessionCreateParams.PaymentIntentData.CaptureMethod.MANUAL)
                				// Per aggiungere dei metadata al PaymentIntent vanno inseriti qui
                				.putMetadata("id_ordine", ordineDB.getId().toString()) // Passo l'id dell'ordine come String
                				.build()
                		)
                .setSuccessUrl("http://localhost:4200/home")  // URL di successo
                .setCancelUrl("http://localhost:4200/404")  // URL di annullamento
                // Per aggiungere dei metadata alla Session posso inserirli qui
                
                .build();

            // Crea la sessione di pagamento su Stripe
            Session session = Session.create(params);

            // Restituisce l'ID della sessione al frontend
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", session.getId());
            return new ResponseEntity<>(responseData, HttpStatus.OK);

        } catch (Exception e) {
        	e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refund(@RequestParam String paymentIntentId) {
        try {
            // Imposta la chiave segreta di Stripe
            Stripe.apiKey = this.secret;

            // Crea i parametri di refund basati sull'ID del PaymentIntent(da dove lo recupero?)
            RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)  // Riferimento al PaymentIntent per il rimborso
                .build();

            // Crea il rimborso su Stripe
            Refund refund = Refund.create(params);

            // Restituisce i dati del rimborso al frontend
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("refundId", refund.getId());
            responseData.put("status", refund.getStatus());
            return new ResponseEntity<>(responseData, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /*@PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        // Verifica la firma e il payload
    	System.out.println("Sono nel metodo che gestisce il webhook");
        Event event;
        try {
        	System.out.println("Sto provando a verificare che il webhook sia di Stripe");
            event = Webhook.constructEvent(payload, sigHeader, this.webhookSecret); // Inserisco il mio webhook secret per assicurarmi che provenga da Stripe
        } catch (SignatureVerificationException e) {
        	System.out.println("Verifica del webhook andata male");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }

        // Gestisci l'evento
        System.out.println("L'evento è stato verificato ed è di tipo: - " + event.getType());
        if ("checkout.session.completed".equals(event.getType())) {
        	System.out.println("La checkout session è stata correttamente completata");
        	// Ottieni la sessione, che è una rappresentazione vuota a meno del payment_intent id
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null && session.getPaymentIntent() != null) {
            	System.out.println("Sono nell'IF che controlla sessione e paymentIntent dove aggiorno l'ordine");
                // Recupera l'ID del payment_intent
                String paymentIntentId = session.getPaymentIntent();

                // Ora fai una chiamata per recuperare il PaymentIntent completo, i metadata sono nel PaymentIntent, non nella Session
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId); // Lancia una StripeException in caso di errore

                // Ora puoi accedere ai metadata
                Integer id_ordine = Integer.valueOf(paymentIntent.getMetadata().get("id_ordine"));
                Ordine o = this.ordService.recuperaOrdine(id_ordine);
                o.setPayment_id(paymentIntentId);
                this.ordService.salvaOrdine(o);
            } else if (session != null && session.getPaymentIntent() == null) {
            	System.out.println("Il payment intent è null");
            } else {
            	System.out.println("La session è null"); // Questo è il problema al momento
            }
        	/*
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
            String paymentIntentId = paymentIntent.getId();
            // Recupero metadata personalizzati
            Integer id_ordine = Integer.valueOf(paymentIntent.getMetadata().get("id_ordine"));
            // Aggiorna l'ordine nel database qui usando paymentIntentId
            Ordine o = this.ordService.recuperaOrdine(id_ordine);
            o.setPayment_id(paymentIntentId);
            this.ordService.salvaOrdine(o);
            *-/
        }else {
        	System.out.println("L'evento è del tipo sbagliato");
        }

        return ResponseEntity.ok("Webhook received");
    }*/
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, this.webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }

        // Logging dell'evento per debug
        //System.out.println("Evento ricevuto: " + event.toJson());

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {
                // Log per verificare i dettagli della sessione
                System.out.println("Sessione ID: " + session.getId());
                System.out.println("Payment Intent ID: " + session.getPaymentIntent());

                String paymentIntentId = session.getPaymentIntent();
                // Recupera il PaymentIntent
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                System.out.println("PaymentIntent recuperato: " + paymentIntent.toJson());
                // Aggiorna l'ordine come prima
                Integer id_ordine = Integer.valueOf(paymentIntent.getMetadata().get("id_ordine"));
                Ordine o = this.ordService.recuperaOrdine(id_ordine);
                o.setPaymentId(paymentIntentId);
                this.ordService.salvaOrdine(o);
            } else {
                System.out.println("Sessione è null");
            }
        } else {
            System.out.println("Tipo di evento non gestito: " + event.getType());
        }

        return ResponseEntity.ok("Webhook received");
    }
    
    @PostMapping("/admin/annulla-pagamento")
    public Map<String, Object> cancelPayment(@RequestBody Map<String, Object> requestData) throws StripeException {
        Stripe.apiKey = secret;

        String paymentIntentId = (String) requestData.get("paymentIntentId");
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        // Recupera l'ordine con il corrispondente paymentIntentId e setta lo stato a ANNULLATO

        // Annulla il pagamento
        PaymentIntent canceledPayment = paymentIntent.cancel();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", canceledPayment.getStatus());
        return responseData;
    }
    
    @PostMapping("/admin/accetta-pagamento")
    public Map<String, Object> capturePayment(@RequestBody Map<String, Object> requestData) throws StripeException {
        Stripe.apiKey = secret;

        String paymentIntentId = (String) requestData.get("paymentIntentId");
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        // Recupera l'ordine con il corrispondente paymentIntentId e setta lo stato a CONFERMATO

        // Cattura il pagamento
        PaymentIntent capturedPayment = paymentIntent.capture();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", capturedPayment.getStatus());
        return responseData;
    }
    
    @PostMapping("/admin/accetta")
    public ResponseEntity<?> accettaPagamento(@RequestBody OrdineFront dto, HttpServletRequest request) throws StripeException{
    	Stripe.apiKey = secret;
    	
    	Ordine ordine = this.ordService.recuperaOrdine(dto.getId());
    	User user = this.userService.findUserById(dto.getId_utente());
    	
    	if(ordine == null || user == null) {
    		System.out.println("L'ordine o l'utente associato non esistono");
    		return ResponseEntity.badRequest().build();
    	}
    	
    	if(ordine.getUtente().getId() != user.getId()) {
    		System.out.println("L'utente recuperato dal DTO non corrisponde all'utente associato all'ordine recuperato dal DTO");
    		return ResponseEntity.badRequest().build();
    	}
    	
    	PaymentIntent paymentIntent = PaymentIntent.retrieve(ordine.getPaymentId());
    	PaymentIntent capturedPayment = paymentIntent.capture();
    	System.out.println(capturedPayment.getStatus());
    	ordine.setStato(StatoOrdine.CONFERMATO);
    	this.ordService.salvaOrdine(ordine);
    	
    	return ResponseEntity.ok(Collections.singletonMap("stato", ordine.getStato()));
    }
    
    @PostMapping("/admin/annulla")
    public ResponseEntity<?> annullaPagamento(@RequestBody OrdineFront dto, HttpServletRequest request) throws StripeException{
    	Stripe.apiKey = secret;
    	
    	Ordine ordine = this.ordService.recuperaOrdine(dto.getId());
    	User user = this.userService.findUserById(dto.getId_utente());
    	
    	if(ordine == null || user == null) {
    		System.out.println("L'ordine o l'utente associato non esistono");
    		return ResponseEntity.badRequest().build();
    	}
    	
    	if(ordine.getUtente().getId() != user.getId()) {
    		System.out.println("L'utente recuperato dal DTO non corrisponde all'utente associato all'ordine recuperato dal DTO");
    		return ResponseEntity.badRequest().build();
    	}
    	
    	PaymentIntent paymentIntent = PaymentIntent.retrieve(ordine.getPaymentId());
    	PaymentIntent capturedPayment = paymentIntent.cancel();
    	System.out.println(capturedPayment.getStatus());
    	ordine.setStato(StatoOrdine.ANNULLATO);
    	this.ordService.salvaOrdine(ordine);
    	
    	return ResponseEntity.ok(Collections.singletonMap("stato", ordine.getStato()));
    }
    
    @GetMapping("/getPublic")
    public ResponseEntity<?> getPublic() {
    	return ResponseEntity.ok(Collections.singletonMap("key", this.stripePublic));
    }
	
}
