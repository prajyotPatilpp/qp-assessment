package com.gba.service;

import com.gba.beans.*;

public interface UserService {
    // temporary API to create user (not needed, just to add some master data)
    ResponseBean createUser(CreateUserRequest createUserReq);

    ResponseBeanGrocery fetchAvailableGroceries(FetchGroceriesRequest fetchGroceriesRequest);

    ResponseBeanOrder createOrder(CreateOrderRequest createOrderRequest);

    ResponseBeanOrderDetails fetchOrderDetails(FetchOrderDetailsRequest fetchOrderDetailsRequest);
}
