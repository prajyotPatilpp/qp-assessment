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
@Table(name="td_order_details_map")
public class TdOrderDetailsMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "order_id")
    private Integer orderId;
    @Column(name = "grocery_item_id")
    private Integer groceryItemId;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "creation_date")
    private Timestamp creationDate;
    @Column(name = "modification_date")
    private Timestamp modificationDate;
}
