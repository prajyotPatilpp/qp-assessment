package com.gba.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseBeanGrocery {

    private String status;
    private Integer statusCode;
    private String statusMsg;
    private List<GroceryResponse> result;
}


