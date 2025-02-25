package dev.MuragliaFood.Beer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.MuragliaFood.Beer.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	User findByUsername(String username);
	User findUserById(Integer id);
}
