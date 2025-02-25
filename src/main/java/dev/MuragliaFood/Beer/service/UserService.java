package dev.MuragliaFood.Beer.service;

import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.MuragliaFood.Beer.dao.UserDAO;
import dev.MuragliaFood.Beer.model.User;
import dev.MuragliaFood.Beer.repository.UserRepository;

@Service
public class UserService implements UserDAO, UserDetailsService{
	
	private final UserRepository repo;
	private final PasswordEncoder encoder;
	
	public UserService(UserRepository repo) {
		this.repo = repo;
		this.encoder = new BCryptPasswordEncoder();
	}
	
	public User saveUser(User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		return repo.save(user);
	}
	
	public User findByUsername(String username) {
		return repo.findByUsername(username);
	}

	@Override
	public Optional<User> optionalFindByUsername(String username) {
		return Optional.ofNullable(repo.findByUsername(username));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("Utente non trovato");
		}
		SimpleGrantedAuthority auth = new SimpleGrantedAuthority(user.getRuolo().name());
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getPassword())
				.authorities(auth)
				.build();
	}

	@Override
	public User findUserById(Integer id) {
		return repo.findUserById(id);
	}
	
}
