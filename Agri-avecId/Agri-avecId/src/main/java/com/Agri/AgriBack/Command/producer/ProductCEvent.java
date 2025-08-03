package com.Agri.AgriBack.Command.producer;

import com.Agri.AgriBack.Command.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

public class ProductCEvent {
    private String eventType;
    private Product product;

    public ProductCEvent(String createProduct, Product productDO) {
        setProduct(productDO);
        setEventType(createProduct);

    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


}
