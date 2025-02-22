package com.gba.beans;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "userId cannot be null")
    private Integer userId;

    @NotEmpty(message = "items list cannot be empty")
    @NotNull(message = "items list cannot be null")
    private List<ItemDetails> items;
}
