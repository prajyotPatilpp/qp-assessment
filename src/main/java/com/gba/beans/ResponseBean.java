package com.gba.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBean {
    String status;
    Integer statusCode;
    String statusMsg;
}
