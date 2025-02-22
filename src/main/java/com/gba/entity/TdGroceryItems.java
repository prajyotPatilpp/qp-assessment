package com.gba.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

@Getter
@Setter
@Entity
@Table(name="td_grocery_items")
public class TdGroceryItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "available_quantity")
    private Integer availableQuantity;
    @Column(name = "status")
    private String status;
    @Column(name = "creation_date")
    private Timestamp creationDate;
    @Column(name = "modification_date")
    private Timestamp modificationDate;
}
