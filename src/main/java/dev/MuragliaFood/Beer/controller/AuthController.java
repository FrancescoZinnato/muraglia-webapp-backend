package dev.MuragliaFood.Beer.controller;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.MuragliaFood.Beer.dto.UserDTO;
import dev.MuragliaFood.Beer.model.User;
import dev.MuragliaFood.Beer.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {
	
    private final AuthUtils auth;
    
    public AuthController(AuthUtils auth) {
        this.auth = auth;
    }
    
    @PostMapping("/registra")
    public ResponseEntity<?> registraUser(@RequestBody User user, HttpServletResponse response) {
    	return auth.registraUser(user, response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginFinal(@RequestBody UserDTO dto, HttpServletRequest request, HttpServletResponse response) {
        String token = auth.estraiTokenDaiCookie(request.getCookies());

        if (token != null) {
            return auth.gestioneValiditaToken(dto, token, response);
        } else {
            return auth.gestioneLogin(dto, response);
        }
    }
    
    @GetMapping("/checkAdmin")
    public ResponseEntity<?> checkAdmin(HttpServletRequest request){
    	return ResponseEntity.ok(Collections.singletonMap("admin", true));
    }
    
    @GetMapping("/checkAuth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request){
    	return ResponseEntity.ok(Collections.singletonMap("auth", true));
    }
    
    @GetMapping("/getUsername")
    public ResponseEntity<?> getUsername(HttpServletRequest request){
    	//String token = auth.estraiTokenDaiCookie(request.getCookies());
    	//String username = auth.getUsernameToken(token);
    	String username = auth.estraiUsernameDaiCookie(request.getCookies());
    	return ResponseEntity.ok(Collections.singletonMap("username", username));
    }
    
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
    	request.getSession().invalidate();
    	this.auth.logout(response, null, request);
    	return ResponseEntity.ok(Collections.singletonMap("out", true));
    }
	
}
