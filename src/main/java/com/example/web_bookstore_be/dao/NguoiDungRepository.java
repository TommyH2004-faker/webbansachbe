package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "users")
public interface NguoiDungRepository extends JpaRepository<User, Integer> {
public boolean existsByUsername(String username);
public boolean existsByEmail(String email);
public User findByUsername(String username);
public User findByEmail(String email);

}
