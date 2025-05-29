package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.OrderDetail;
import com.example.web_bookstore_be.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "review")
public interface SuDanhGiaRepository extends JpaRepository<Review, Integer> {
    public Review findReviewByOrderDetail(OrderDetail orderDetail);
    public long countBy();

}
