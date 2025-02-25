package dev.MuragliaFood.Beer.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import dev.MuragliaFood.Beer.service.UserService;
import dev.MuragliaFood.Beer.util.JwtUtils;

import static org.springframework.security.config.Customizer.*;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{
	
	private final UserService service;
	private final JwtUtils util;
	
	public SecurityConfig(UserService service, JwtUtils util) {
		this.service = service;
		this.util = util;
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://api.stripe.com"));
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Stripe-Signature"));
	    configuration.setAllowCredentials(true);
	    
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            //.csrf(AbstractHttpConfigurer::disable)
        	.cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/prodotti/admin/**").hasAuthority("ROLE_ADMIN")
            	.requestMatchers("/auth/checkAdmin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/auth/checkAuth/**").authenticated()
                .requestMatchers("/stripe/webhook").permitAll() // Inutile?
                .requestMatchers("/stripe/checkout").authenticated()
                .requestMatchers("/stripe/refund").authenticated()
                .requestMatchers("/stripe/admin/**").hasAuthority("ROLE_ADMIN")
            	.requestMatchers("/ord/admin/**").hasAuthority("ROLE_ADMIN")
                //.anyRequest().authenticated()
            	.anyRequest().permitAll()
            )
            /*.logout(logout -> logout
	            .invalidateHttpSession(true) // Invalida la sessione
	            .clearAuthentication(true) // Rimuove le credenziali di autenticazione
	            .deleteCookies("JSESSIONID") // Rimuove il cookie JSESSIONID
	            .logoutUrl("/auth/logout") // URL di logout
	            .permitAll()
            )*/
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(new JwtAuthenticationFilter(service, util), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

	/*
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
    */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Recupera l'AuthenticationManager dal contesto di Spring
    }
	
}
