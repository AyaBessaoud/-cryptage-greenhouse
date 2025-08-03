package com.Agri.AgriBack.Command.Controller;

import com.Agri.AgriBack.Command.producer.ProductCEvent;
import com.Agri.AgriBack.Command.entity.Product;
import com.Agri.AgriBack.Command.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Product CreateProduct (@RequestBody ProductCEvent product){
        return productService.CreateProduct(product);
    }

    @PutMapping("/{id}")
    public Product UpdateProduct(@PathVariable long id,@RequestBody ProductCEvent product){
        return productService.UpdateProduct(id, product);
    }
}
