package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Query.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQEvent  { //implements Serializable

    //private static final long serialVersionUID = 1L;
    private String eventType;
    private Product product;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
