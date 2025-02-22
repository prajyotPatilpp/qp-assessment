package com.gba.controller;

import com.gba.beans.*;
import com.gba.entity.TdUserTable;
import com.gba.repository.TdUserTableDao;
import com.gba.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger("UserController");

    @Autowired
    private UserService userService;

    // Temporary API to create a user (not required in the assessment, but added to populate master data in the system)
    @Operation(summary = "Temporary API to create a user (not required in the assessment, but added to populate master data in the system)")
    @PostMapping("/createUser")
    public ResponseEntity<ResponseBean> createUser (@RequestBody @Valid CreateUserRequest createUserReq) {
        logger.info("Entered method createUser ");
        ResponseBean resp = new ResponseBean();
        try {
            ResponseBean responseMessage = userService.createUser(createUserReq);
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            logger.error("Error occurred while processing createUser" + e);
            resp.setStatus("Failed");
            resp.setStatusCode(500);
            resp.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    //API to fetch the list of available grocery items
    @Operation(summary = "API to fetch the list of available grocery items")
    @PostMapping("/fetchAvailableGroceries")
    public ResponseEntity<ResponseBeanGrocery> fetchAvailableGroceries(@RequestBody @Valid FetchGroceriesRequest fetchGroceriesRequest) {
        logger.info("Entered method fetchAvailableGroceries ");
        ResponseBeanGrocery response;
        try {
            response = userService.fetchAvailableGroceries(fetchGroceriesRequest);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error occurred while executing service method fetchAvailableGroceries ", e);
            ResponseBeanGrocery errorResponse = new ResponseBeanGrocery();
            errorResponse.setStatus("Failed");
            errorResponse.setStatusCode(500);
            errorResponse.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //API to place an order (multiple items can be booked in a single order)
    @Operation(summary = "API to place an order (multiple items can be booked in a single order)")
    @PostMapping("/createOrder")
    public ResponseEntity<ResponseBeanOrder> createOrder (@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        logger.info("Entered method createOrder ");
        ResponseBeanOrder resp = new ResponseBeanOrder();
        try {
            ResponseBeanOrder responseMessage = userService.createOrder(createOrderRequest);
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            logger.error("Error occurred while processing createOrder" + e);
            resp.setStatus("Failed");
            resp.setStatusCode(500);
            resp.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    // API to fetch order history for a user
    @Operation(summary = "API to fetch order history for a user")
    @PostMapping("/fetchOrderDetails")
    public ResponseEntity<ResponseBeanOrderDetails> fetchOrderDetails(@RequestBody @Valid FetchOrderDetailsRequest fetchOrderDetailsRequest) {
        logger.info("Entered method fetchOrderDetails ");
        ResponseBeanOrderDetails response;
        try {
            response = userService.fetchOrderDetails(fetchOrderDetailsRequest);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error occurred while executing service method fetchOrderDetails ", e);
            ResponseBeanOrderDetails errorResponse = new ResponseBeanOrderDetails();
            errorResponse.setStatus("Failed");
            errorResponse.setStatusCode(500);
            errorResponse.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

