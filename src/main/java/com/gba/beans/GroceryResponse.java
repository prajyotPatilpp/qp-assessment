package com.gba.beans;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
public class GroceryResponse {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer availableQuantity;
    private String status;
    private Timestamp creationDate;
    private Timestamp modificationDate;

    public GroceryResponse(Integer id, String name, BigDecimal price, Integer availableQuantity,
                           String status, Timestamp creationDate, Timestamp modificationDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.status = status;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }
}
