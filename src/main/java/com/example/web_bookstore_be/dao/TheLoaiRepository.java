package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "genres")
public interface TheLoaiRepository extends JpaRepository<Genre, Integer> {
}
