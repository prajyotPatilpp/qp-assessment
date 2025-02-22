package com.gba.serviceImpl;

import com.gba.beans.*;
import com.gba.entity.TdGroceryItems;
import com.gba.entity.TdOrderDetailsMap;
import com.gba.entity.TdOrderMap;
import com.gba.entity.TdUserTable;
import com.gba.repository.TdGroceryItemDao;
import com.gba.repository.TdOrderDetailsMapDao;
import com.gba.repository.TdOrderMapDao;
import com.gba.repository.TdUserTableDao;
import com.gba.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TdUserTableDao tdUserTableDao;

    @Value("${fetch.limit}")
    private int limit;

    private final TdGroceryItemDao tdGroceryItemDao;

    public UserServiceImpl(TdGroceryItemDao tdGroceryItemDao) {
        this.tdGroceryItemDao = tdGroceryItemDao;
    }

    @Autowired
    private TdOrderMapDao tdOrderMapDao;

    @Autowired
    private TdOrderDetailsMapDao tdOrderDetailsMapDao;

    private static final Logger logger = LoggerFactory.getLogger("UserServiceImpl");

    @Override
    public ResponseBean createUser(CreateUserRequest createUserReq) {
        logger.info("Entering createUser :: UserServiceImpl with mobileNumber: {}", createUserReq.getMobileNumber());
        ResponseBean response = new ResponseBean();

        try {
            if (createUserReq.getName() == null || createUserReq.getMobileNumber() == null || createUserReq.getEmailId() == null) {
                response.setStatus("Failure");
                response.setStatusMsg("Invalid input! Name, mobile number, and email are required.");
                response.setStatusCode(400);
                logger.error("User creation failed due to missing required fields.");
                return response;
            }

            TdUserTable tdUserTable = new TdUserTable();
            tdUserTable.setName(createUserReq.getName());
            tdUserTable.setMobileNumber(createUserReq.getMobileNumber());
            tdUserTable.setEmailId(createUserReq.getEmailId());
            tdUserTable.setCreationDate(LocalDateTime.now());
            tdUserTable.setModificationDate(LocalDateTime.now());
            tdUserTable.setStatus("ACTIVE");

            tdUserTableDao.save(tdUserTable);

            response.setStatus("Success");
            response.setStatusMsg("User created successfully!");
            response.setStatusCode(200);
            logger.info("User created successfully with ID: {}", tdUserTable.getId());

        } catch (Exception e) {
            logger.error("Error occurred while creating user: {}", e.getMessage(), e);
            response.setStatus("Failure");
            response.setStatusMsg("An error occurred while creating the user. Please try again.");
            response.setStatusCode(500);
        }
        return response;
    }


    @Override
    @Cacheable(value = "availableGroceries", key = "#fetchGroceriesRequest.name + '_' + #fetchGroceriesRequest.pageNumber")
    public ResponseBeanGrocery fetchAvailableGroceries(FetchGroceriesRequest fetchGroceriesRequest) {
        logger.info("entering fetchAvailableGroceries :: ActionServiceImpl with Grocery name : " + fetchGroceriesRequest.getName());

        // I will be using limit and offset for pagination. (Limit specifies how many records to return per page, while pageNumber acts as an offset to skip records for the next page.)
        // For simplicity, I am assuming a limit of 10

        Integer offset = fetchGroceriesRequest.getPageNumber() * limit; // Offset calculation

        ResponseBeanGrocery response = new ResponseBeanGrocery();
        List<TdGroceryItems> groceryList;

        // If a name is specified in the request, fetch the record with that name
        if(fetchGroceriesRequest.getName()!=null){
            try {
                groceryList = tdGroceryItemDao.findByGroceryName(fetchGroceriesRequest.getName());
            } catch (Exception e) {
                logger.error("Error occurred while retrieving groceries using name from DB");
                response.setStatus("Failed");
                response.setStatusCode(500);
                response.setStatusMsg("Error occurred while retrieving groceries using name from DB");
                return response;
            }}
        else{
            try{
                groceryList = tdGroceryItemDao.findAllAvailableGroceries(limit, offset);} // If no name is specified, fetch all records sorted by name.
            catch (Exception e){
                logger.error("Error occurred while retrieving groceries from DB");
                response.setStatus("Failed");
                response.setStatusCode(500);
                response.setStatusMsg("Error occurred while retrieving groceries from DB");
                return response;
            }
        }

        List<GroceryResponse> groceryResponseList = groceryList.stream()
                .map(grocery -> new GroceryResponse(
                        grocery.getId(),
                        grocery.getName(),
                        grocery.getPrice(),
                        grocery.getAvailableQuantity(),
                        grocery.getStatus(),
                        grocery.getCreationDate(),
                        grocery.getModificationDate()
                ))
                .collect(Collectors.toList());

        response.setStatus("Success");
        response.setStatusCode(200);
        response.setStatusMsg("Available Groceries fetched successfully");
        response.setResult(groceryResponseList);
        logger.info("Available Groceries fetched successfully");
        return response;
    }

    @Transactional
    @Override
    @CacheEvict (value = "orderDetails", allEntries = true)
    public ResponseBeanOrder createOrder(CreateOrderRequest createOrderRequest){
        logger.info("entering createOrder :: ActionServiceImpl with User ID : " + createOrderRequest.getUserId());
        ResponseBeanOrder response = new ResponseBeanOrder();

        List<TdOrderDetailsMap> orderDetailsList = new ArrayList<>();
        List<TdGroceryItems> updatedGroceryItems = new ArrayList<>();

        try {
            for (ItemDetails itemDetails : createOrderRequest.getItems()) {
                TdGroceryItems groceryItem = tdGroceryItemDao.findGroceryById(itemDetails.getItemId()); // check if the requested item exists or not

                if (groceryItem == null) {
                    logger.error("Grocery item not found for ID: {}", itemDetails.getItemId());
                    throw new RuntimeException("Grocery item not found for ID: " + itemDetails.getItemId());
                }

                // retrieve the available quantity to verify if the requested amount is in stock
                Integer availableQuantity = groceryItem.getAvailableQuantity();
                if (availableQuantity == null || availableQuantity < itemDetails.getQuantity()) {
                    response.setStatus("failure");
                    response.setStatusMsg("Insufficient stock for item ID: " + itemDetails.getItemId());
                    response.setStatusCode(400);
                    logger.error("Insufficient stock for item ID: {}", itemDetails.getItemId());
                    return response;
                }
            }

            // create order entry
            TdOrderMap order = new TdOrderMap();
            order.setUserId(createOrderRequest.getUserId());
            order.setStatus("Pending"); // Set status to 'Pending' since payment has not been completed yet
            order.setCreationDate(new Timestamp(System.currentTimeMillis()));
            order.setModificationDate(new Timestamp(System.currentTimeMillis()));
            order.setTotalPrice(null);
            TdOrderMap orderDetails = tdOrderMapDao.save(order);

            // create order details entry
            for (ItemDetails itemDetails : createOrderRequest.getItems()) {
                TdOrderDetailsMap tdOrderDetailsMap = new TdOrderDetailsMap();
                tdOrderDetailsMap.setOrderId(orderDetails.getId());
                tdOrderDetailsMap.setGroceryItemId(itemDetails.getItemId());
                tdOrderDetailsMap.setQuantity(itemDetails.getQuantity());

                TdGroceryItems groceryItem = tdGroceryItemDao.findGroceryById(itemDetails.getItemId());

                tdOrderDetailsMap.setPrice(groceryItem.getPrice().multiply(BigDecimal.valueOf(itemDetails.getQuantity())));
                tdOrderDetailsMap.setCreationDate(new Timestamp(System.currentTimeMillis()));
                tdOrderDetailsMap.setModificationDate(new Timestamp(System.currentTimeMillis()));

                orderDetailsList.add(tdOrderDetailsMap);

                // subtract ordered quantity from stock
                groceryItem.setAvailableQuantity(groceryItem.getAvailableQuantity() - itemDetails.getQuantity());
                updatedGroceryItems.add(groceryItem);
            }

            tdOrderDetailsMapDao.saveAll(orderDetailsList);
            tdGroceryItemDao.saveAll(updatedGroceryItems);

            BigDecimal totalPrice = tdOrderDetailsMapDao.findTotalPriceByOrderId(orderDetails.getId());
            orderDetails.setTotalPrice(totalPrice);
            tdOrderMapDao.save(orderDetails);

            CreateOrderResponse createOrderResponse = new CreateOrderResponse();
            createOrderResponse.setOrderId(order.getId());
            createOrderResponse.setStatus(order.getStatus());

            response.setStatus("success");
            response.setStatusMsg("Order placed successfully!");
            response.setStatusCode(200);
            response.setResult(createOrderResponse);
            logger.info("Order placed successfully for User ID: {}", createOrderRequest.getUserId());
        } catch (RuntimeException e) {
            logger.error("Validation error: {}", e.getMessage(), e);
            response.setStatus("failure");
            response.setStatusMsg(e.getMessage());
            response.setStatusCode(400);
        } catch (Exception e) {
            logger.error("Error while creating order: {}", e.getMessage(), e);
            response.setStatus("failure");
            response.setStatusMsg("Failed to place order. Please try again!");
            response.setStatusCode(500);
        }
        return response;
    }

    @Override
    @Cacheable(value = "orderDetails", key = "#fetchOrderDetailsRequest.userId + '_' + #fetchOrderDetailsRequest.orderId + '_' + #fetchOrderDetailsRequest.pageNumber")
    public ResponseBeanOrderDetails fetchOrderDetails(FetchOrderDetailsRequest fetchOrderDetailsRequest) {
        logger.info("Entering fetchOrderDetails :: UserServiceImpl with userID: {}", fetchOrderDetailsRequest.getUserId());

        ResponseBeanOrderDetails response = new ResponseBeanOrderDetails();
        List<TdOrderMap> orderList;

        try {
            if (fetchOrderDetailsRequest.getOrderId() != null) {
                orderList = tdOrderMapDao.findByOrderId(fetchOrderDetailsRequest.getOrderId(), fetchOrderDetailsRequest.getUserId());
            } else {
                int offset = fetchOrderDetailsRequest.getPageNumber() * limit;
                orderList = tdOrderMapDao.findAllOrdersForUser(fetchOrderDetailsRequest.getUserId(), limit, offset);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving orders from DB", e);
            response.setStatus("Failed");
            response.setStatusCode(500);
            response.setStatusMsg("Error occurred while retrieving orders from DB");
            return response;
        }

        if (orderList.isEmpty()) {
            logger.error("No orders found for userID: {}", fetchOrderDetailsRequest.getUserId());
            response.setStatus("Failed");
            response.setStatusCode(500);
            response.setStatusMsg("No orders found for the user");
            return response;
        }

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (TdOrderMap order : orderList) {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            orderResponse.setUserId(order.getUserId());
            orderResponse.setTotalPrice(order.getTotalPrice());
            orderResponse.setStatus(order.getStatus());

            List<TdOrderDetailsMap> orderDetails = tdOrderDetailsMapDao.findByOrderId(order.getId());
            List<Items> itemsList = new ArrayList<>();

            for (TdOrderDetailsMap orderDetail : orderDetails) {
                TdGroceryItems groceryItem = tdGroceryItemDao.findGroceryById(orderDetail.getGroceryItemId());

                if (groceryItem != null) {
                    Items item = new Items();
                    item.setItemId(orderDetail.getGroceryItemId());
                    item.setName(groceryItem.getName());
                    item.setQuantity(orderDetail.getQuantity());
                    item.setPrice(orderDetail.getPrice());

                    itemsList.add(item);
                }
            }

            orderResponse.setItems(itemsList);
            orderResponses.add(orderResponse);
        }

        response.setStatus("Success");
        response.setStatusCode(200);
        response.setStatusMsg("Fetched successfully");
        response.setResult(orderResponses);

        logger.info("Orders fetched successfully for User ID: {}", fetchOrderDetailsRequest.getUserId());
        return response;
    }

}
