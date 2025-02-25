package dev.MuragliaFood.Beer.util;

import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import dev.MuragliaFood.Beer.model.User;
import dev.MuragliaFood.Beer.dto.UserDTO;
import dev.MuragliaFood.Beer.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthUtils {
	
	private final AuthenticationManager authenticator;
	private final UserService service;
    private final JwtUtils util;
    private final PasswordEncoder encoder;
    
    public AuthUtils(UserService service, JwtUtils util, AuthenticationManager authenticator) {
    	this.service = service;
    	this.util = util;
    	this.authenticator = authenticator;
    	this.encoder = new BCryptPasswordEncoder();
    }
    
    public ResponseEntity<?> registraUser(@RequestBody User user, HttpServletResponse response) {
        if (service.optionalFindByUsername(user.getUsername()).isPresent() || 
        		service.optionalFindByUsername(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username o email già esistenti");
        }
        service.saveUser(user);
        UserDetails userDetails = service.loadUserByUsername(user.getUsername());
        Authentication auth = creaAuthenticationNelContext(user.getUsername(), user.getPassword(), userDetails.getAuthorities());
        UserDetails userDetailsAuth = (UserDetails) auth.getPrincipal();
        String token = util.generateToken(userDetailsAuth);
        creaCookieToken(token, response);
        creaCookieUsername(util.extractUsername(token), response);
        return ResponseEntity.ok(Collections.singletonMap("messaggio", "Utente registrato con successo")); //Restituisco come JSON perchè me lo richiede il frontend Angular
    }
    
	public String getUsernameToken(String token) {
		return util.extractUsername(token);
	}
	
	public String estraiTokenDaiCookie(Cookie[] cookies) {
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("token".equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}
	
	public String estraiUsernameDaiCookie(Cookie[] cookies) {
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("username".equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}

	public ResponseEntity<?> gestioneValiditaToken(@RequestBody UserDTO dto, String token, HttpServletResponse response) {
	    UserDetails user = service.loadUserByUsername(dto.getUsername());
	    if (user == null) {
	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido o utente non trovato");
	    }
	    if (token != null && !util.validateToken(token, user)) {
            Authentication auth = creaAuthenticationNelContext(dto.getUsername(), dto.getPassword(), user.getAuthorities());
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
	        String newToken = util.generateToken(userDetails);
	        aggiornaCookie(newToken, response);
	        aggiornaCookieUsername(util.extractUsername(newToken), response);
	        return ResponseEntity.ok(Collections.singletonMap("message", "Token aggiornato con successo e login effettuato"));
	    }
        creaAuthenticationNelContextVoid(dto.getUsername(), dto.getPassword(), user.getAuthorities());
	    return ResponseEntity.ok(Collections.singletonMap("message", "Login effettuato con successo token valido"));
	}

	public ResponseEntity<?> gestioneLogin(@RequestBody UserDTO dto, HttpServletResponse response) {
	    //UserDetails user = service.loadUserByUsername(dto.getUsername());
		UserDetails user = service.loadUserByUsername(dto.getUsername());
	    if (user != null && encoder.matches(dto.getPassword(), user.getPassword())) {
	    	/*Authentication auth = authenticator.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));*/
            Authentication auth = creaAuthenticationNelContext(dto.getUsername(), dto.getPassword(), user.getAuthorities());
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
	        String token = util.generateToken(userDetails);
	        creaCookieToken(token, response);
	        creaCookieUsername(util.extractUsername(token), response);
	        return ResponseEntity.ok(Collections.singletonMap("message", "Login effettuato con successo nuovo token"));
	    }
	    return ResponseEntity.status(401).body("Username o password invalidi");
	}
	
	public void logout(HttpServletResponse response, String x, HttpServletRequest request) {
		this.invalidaCookieToken(x, response);
		this.invalidaCookieUsername(x, response);
		Cookie cookie = new Cookie("JSESSIONID", null);
	    cookie.setPath("/");
	    cookie.setHttpOnly(true);
	    cookie.setMaxAge(0);
	    response.addCookie(cookie);
	    SecurityContextHolder.getContext().setAuthentication(null);
	    SecurityContextHolder.clearContext();
	}

	private void aggiornaCookie(String token, HttpServletResponse response) {
	    invalidaCookieToken(token, response);
	    creaCookieToken(token, response);
	}
	
	private void aggiornaCookieUsername(String username, HttpServletResponse response) {
	    invalidaCookieUsername(username, response);
	    creaCookieUsername(username, response);
	}
	
	private Authentication creaAuthenticationNelContext(String username, String password, Collection<? extends GrantedAuthority> claims) {
		Authentication auth = authenticator.authenticate(new UsernamePasswordAuthenticationToken(username, password, claims));
		SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}
	
	private void creaAuthenticationNelContextVoid(String username, String password, Collection<? extends GrantedAuthority> claims) {
		Authentication auth = authenticator.authenticate(new UsernamePasswordAuthenticationToken(username, password, claims));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	private void creaCookieToken(String token, HttpServletResponse response) {
	    Cookie cookie = new Cookie("token", token);
	    cookie.setHttpOnly(true);
	    cookie.setSecure(true);
	    cookie.setPath("/");
	    cookie.setMaxAge(1 * 24 * 60 * 60);
	    response.addCookie(cookie);
	}
	
	private void invalidaCookieToken(String token, HttpServletResponse response) {
	    Cookie cookie = new Cookie("token", token);
	    cookie.setHttpOnly(true);
	    cookie.setSecure(true);
	    cookie.setPath("/");
	    cookie.setMaxAge(0);
	    response.addCookie(cookie);
	}
	
	private void creaCookieUsername(String username, HttpServletResponse response) {
	    Cookie cookie = new Cookie("username", username);
	    cookie.setHttpOnly(false);
	    cookie.setSecure(true);
	    cookie.setPath("/");
	    cookie.setMaxAge(1 * 24 * 60 * 60);
	    response.addCookie(cookie);
	}
	
	private void invalidaCookieUsername(String username, HttpServletResponse response) {
	    Cookie cookie = new Cookie("username", username);
	    cookie.setHttpOnly(false);
	    cookie.setSecure(true);
	    cookie.setPath("/");
	    cookie.setMaxAge(0);
	    response.addCookie(cookie);
	}
	
}
