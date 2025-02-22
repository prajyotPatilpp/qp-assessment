package com.gba.beans;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Integer orderId;
    private Integer userId;
    private BigDecimal totalPrice;
    private String status;
    private List<Items> items;
}
