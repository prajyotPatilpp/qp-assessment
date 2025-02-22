package com.gba.beans;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchGroceriesRequest {

    private String name;

    @NotNull(message = "pageNumber cannot be null")
    private Integer pageNumber;
}
