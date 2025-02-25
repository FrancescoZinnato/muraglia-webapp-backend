package dev.MuragliaFood.Beer.dao;

import java.util.Optional;

import dev.MuragliaFood.Beer.model.User;

public interface UserDAO {
	Optional<User> optionalFindByUsername(String username);
	User findUserById(Integer id);
}
