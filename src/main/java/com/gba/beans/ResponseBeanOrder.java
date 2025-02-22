package com.gba.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBeanOrder {

    private String status;
    private Integer statusCode;
    private String statusMsg;
    private CreateOrderResponse result;

}
