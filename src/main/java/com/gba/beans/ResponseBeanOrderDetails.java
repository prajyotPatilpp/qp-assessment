package com.gba.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseBeanOrderDetails {


    private String status;
    private Integer statusCode;
    private String statusMsg;
    private List<OrderResponse> result;

}
