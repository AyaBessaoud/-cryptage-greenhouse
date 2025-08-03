package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.producer.ProductCEvent;
import com.Agri.AgriBack.Command.entity.Product;
import com.Agri.AgriBack.Command.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("CommandProductService")
public class ProductService {


    @Autowired
    private ProductRepo repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public Product CreateProduct (ProductCEvent productEvent){
        if (productEvent.getProduct() == null) {
            throw new IllegalArgumentException("Le produit ne peut pas être null");
        }
        Product productDO = repository.save(productEvent.getProduct());
        ProductCEvent event=new ProductCEvent("CreateProduct", productDO);
        kafkaTemplate.send("product-event-topic", event);
        return productDO;
    }

    public Product UpdateProduct(long id, ProductCEvent productEvent){
        Product existingProduct = repository.findById(id).get();
        Product newProduct=productEvent.getProduct();
        existingProduct.setName(newProduct.getName());
        existingProduct.setDescription(newProduct.getDescription());
        existingProduct.setPrice(newProduct.getPrice());
        Product productDO = repository.save(existingProduct);
        ProductCEvent event=new ProductCEvent("UpdateProduct", productDO);
        kafkaTemplate.send("product-event-topic", event);
        return productDO;
    }
}
