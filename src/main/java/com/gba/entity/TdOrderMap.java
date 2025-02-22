package com.gba.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.type.descriptor.sql.internal.BinaryFloatDdlType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

@Getter
@Setter
@Entity
@Table(name="td_order_map")
public class TdOrderMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Column(name = "status")
    private String status;
    @Column(name = "creation_date")
    private Timestamp creationDate;
    @Column(name = "modification_date")
    private Timestamp modificationDate;

}
