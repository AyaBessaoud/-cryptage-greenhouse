package com.Agri.AgriBack.Query.services;

import com.Agri.AgriBack.Command.producer.ProductCEvent;
import com.Agri.AgriBack.Query.entity.Product;

import com.Agri.AgriBack.Query.repository.ProductQRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductQService {

    @Autowired
    private ProductQRepo repo;

    public List<Product> getProducts() {
        return repo.findAll();
    }

    @KafkaListener(topics = "product-event-topic", groupId = "Agri-group", properties = {"spring.json.trusted.packages=*"})
    public void processProductEvents(@Payload ProductCEvent productEvent) {
        System.out.println("Event reçu depuis Kafka : " + productEvent);
        if (productEvent == null || productEvent.getProduct() == null) {
            System.out.println("Erreur : Réception d'un event avec un produit null !");
        }

        com.Agri.AgriBack.Command.entity.Product commandProduct = productEvent.getProduct();

        Product product = new Product();
        product.setId(commandProduct.getId().toString());
        System.out.println("✅ Produit non null, ID : " + commandProduct.getId());
        product.setName(commandProduct.getName());
        product.setPrice(commandProduct.getPrice());
        product.setDescription(commandProduct.getDescription());

        if ("CreateProduct".equals(productEvent.getEventType())) {
            repo.save(product);
            System.out.println("✅ Produit créé avec succès : " + product.getId());
        } else if ("UpdateProduct".equals(productEvent.getEventType())) {
            Optional<Product> existingProductOpt = repo.findById(product.getId());
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();
                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setDescription(product.getDescription());
                repo.save(existingProduct);
                System.out.println("Produit mis à jour : " + product.getId());
            } else {
                System.out.println("⚠Erreur : Produit non trouvé pour mise à jour : " + product.getId());
            }
        }
    }

}