package com.Agri.AgriBack.Command.repository;

import com.Agri.AgriBack.Command.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
