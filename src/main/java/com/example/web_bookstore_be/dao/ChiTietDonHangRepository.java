package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Order;
import com.example.web_bookstore_be.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "order-detail")
public interface ChiTietDonHangRepository extends JpaRepository<OrderDetail, Integer> {

    public List<OrderDetail> findOrderDetailsByOrder(Order order);
}
