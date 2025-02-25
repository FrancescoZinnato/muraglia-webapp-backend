package dev.MuragliaFood.Beer.model;


import java.util.List;
import java.util.Objects;

import dev.MuragliaFood.Beer.util.Ruolo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//@Document (collection = "admin")
@Entity
@Table (name = "utente")
public class User {
	@Id
	@Column (name = "idutente")
	@GeneratedValue (strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column (name = "username", nullable = false)
	private String username;
	@Column (name = "password", nullable = false)
	private String password;
	@Column (name = "email", nullable = false)
	private String email;
	@Enumerated (EnumType.STRING)
	private Ruolo ruolo;
	@OneToMany (mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Ordine> ordini; // Sostituire con lista id ordini?
	
	public User(Integer id, String username, String password, String email) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.ruolo = Ruolo.ROLE_USER;
	}
	
	public User(String username, String password, String email) {
		super();
		this.id = 0;
		this.username = username;
		this.password = password;
		this.email = email;
		this.ruolo = Ruolo.ROLE_USER;
	}
	
	public User() {
		this.id = 0;
		this.username = "";
		this.password = "";
		this.email = "";
		this.ruolo = Ruolo.ROLE_USER;
	}

	public Integer getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, id, ordini, password, ruolo, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && Objects.equals(id, other.id)
				&& Objects.equals(ordini, other.ordini) && Objects.equals(password, other.password)
				&& ruolo == other.ruolo && Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email + "]";
	}
	
}
