package com.gba.controller;

import com.gba.beans.*;
import com.gba.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger("AdminController");

    @Autowired
    private AdminService adminService;

    // API to add or update a grocery item in the system
    // This single API handles both adding and updating grocery items.
    // If `updateFlag` is true, it updates the item based on the provided ID; otherwise, it adds a new entry.
    // This API also does the work of managing inventory (Update Stock Level) by allowing the admin to update the quantity(stock level) for grocery item
    @Operation(summary = "API to add or update a grocery item in the system")
    @PostMapping("/addOrUpdateGroceryItem")
    public ResponseEntity<ResponseBean> addOrUpdateGroceryItem (@RequestBody @Valid AddGroceryRequest addGroceryRequest) {
        logger.info("Entered method addOrUpdateGroceryItem ");
        ResponseBean resp = new ResponseBean();
        try {
            ResponseBean responseMessage = adminService.addOrUpdateGroceryItem(addGroceryRequest);
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            logger.error("Error occurred while processing addOrUpdateGroceryItem" + e);
            resp.setStatus("Failed");
            resp.setStatusCode(500);
            resp.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }


    // API to fetch all grocery items from the system
    @Operation(summary = "API to fetch all grocery items from the system")
    @PostMapping("/fetchGroceries")
    public ResponseEntity<ResponseBeanGrocery> fetchGroceries(@RequestBody @Valid FetchGroceriesRequest fetchGroceriesRequest) {
        logger.info("Entered method fetchGroceries ");
        ResponseBeanGrocery response;
        try {
            response = adminService.fetchGroceries(fetchGroceriesRequest);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error occurred while executing service method fetchGroceries ", e);
            ResponseBeanGrocery errorResponse = new ResponseBeanGrocery();
            errorResponse.setStatus("Failed");
            errorResponse.setStatusCode(500);
            errorResponse.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // API to remove(mark NotAvailable) grocery items from inventory (system)
    @Operation(summary = "API to remove(mark NotAvailable) grocery items from inventory (system)")
    @PostMapping("/removeGroceryFromSystem")
    public ResponseEntity<ResponseBean> removeGroceryFromSystem(@RequestBody @Valid RemoveGroceryRequest removeGroceryRequest) {
        logger.info("Entered method removeGroceryFromSystem ");
        ResponseBean response;
        try {
            response = adminService.removeGroceryFromSystem(removeGroceryRequest);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error occurred while executing service method removeGroceryFromSystem ", e);
            ResponseBean errorResponse = new ResponseBean();
            errorResponse.setStatus("Failed");
            errorResponse.setStatusCode(500);
            errorResponse.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
