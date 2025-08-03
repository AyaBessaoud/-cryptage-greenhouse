package com.Agri.AgriBack.Query.Contoller;

import com.Agri.AgriBack.Query.entity.Product;
import com.Agri.AgriBack.Query.services.ProductQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/products")
@RestController
public class ProductQueryController {

    @Autowired
    private ProductQService productService;

    @GetMapping
    public List<Product> fetchAllProducts(){
        return productService.getProducts();
    }
}
