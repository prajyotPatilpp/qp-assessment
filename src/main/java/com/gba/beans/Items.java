package com.gba.beans;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Items {

    private Integer itemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
