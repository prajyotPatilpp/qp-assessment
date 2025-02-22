package com.gba.beans;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchOrderDetailsRequest {

    @NotNull ( message = "userId is required!")
    private Integer userId;

    private Integer orderId;

    @NotNull (message = "pageNumber is required!")
    private Integer pageNumber;
}
