package com.gba.serviceImpl;

import com.gba.beans.*;
import com.gba.entity.TdGroceryItems;
import com.gba.repository.TdGroceryItemDao;
import com.gba.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger("UserServiceImpl");

    @Value("${fetch.limit}")
    private int limit;

    private final TdGroceryItemDao tdGroceryItemDao;

    public AdminServiceImpl(TdGroceryItemDao tdGroceryItemDao) {
        this.tdGroceryItemDao = tdGroceryItemDao;
    }

    @Override
    @CacheEvict(value = {"allGroceries", "availableGroceries"}, allEntries = true) // @CachePut could also be used, but since updates are frequent and data consistency is crucial, I will be using @CacheEvict
    public ResponseBean addOrUpdateGroceryItem(AddGroceryRequest addGroceryRequest) {
        logger.info("Entering addOrUpdateGroceryItem :: AdminServiceImpl");
        ResponseBean response = new ResponseBean();
        boolean isUpdate = addGroceryRequest.getUpdateFlag().equalsIgnoreCase("true");

        try {
            TdGroceryItems tdGroceryItems;

            if (isUpdate)
            {
                Optional<TdGroceryItems> existingItem = tdGroceryItemDao.findById(addGroceryRequest.getId());

                if (existingItem.isPresent()) {
                    tdGroceryItems = existingItem.get();
                    logger.info("Updating existing grocery item with ID: {}", addGroceryRequest.getId());
                } else {
                    response.setStatus("failure");
                    response.setStatusMsg("Grocery item not found for update!");
                    response.setStatusCode(404);
                    logger.error("Grocery item with ID {} not found for update", addGroceryRequest.getId());
                    return response;
                }
            } else {
                tdGroceryItems = new TdGroceryItems();
                tdGroceryItems.setStatus("Available");
                tdGroceryItems.setCreationDate(new Timestamp(System.currentTimeMillis()));
                logger.info("Adding new grocery item: {}", addGroceryRequest.getName());
            }

            tdGroceryItems.setName(addGroceryRequest.getName());
            tdGroceryItems.setPrice(addGroceryRequest.getPrice());
            tdGroceryItems.setAvailableQuantity(addGroceryRequest.getAvailableQuantity());
            tdGroceryItems.setModificationDate(new Timestamp(System.currentTimeMillis()));

            tdGroceryItemDao.save(tdGroceryItems);

            response.setStatus("success");
            response.setStatusMsg(isUpdate ? "Grocery item updated successfully!" : "New grocery item added successfully!");
            response.setStatusCode(200);
            logger.info(isUpdate ? "Grocery item updated successfully!" : "New grocery item added successfully!");

        } catch (Exception e) {
            logger.error("Error {} grocery item: {}", isUpdate ? "updating" : "adding", e.getMessage(), e);
            response.setStatus("failure");
            response.setStatusMsg("Failed to process grocery item. Please try again!");
            response.setStatusCode(500);
        }
        return response;
    }

    @Override
    @Cacheable(value = "allGroceries", key = "#fetchGroceriesRequest.name + '_' + #fetchGroceriesRequest.pageNumber")
    public ResponseBeanGrocery fetchGroceries(FetchGroceriesRequest fetchGroceriesRequest) {
        logger.info("entering fetchGroceries :: ActionServiceImpl with Grocery name : " + fetchGroceriesRequest.getName());

        // I will be using limit and offset for pagination. (Limit specifies how many records to return per page, while pageNumber acts as an offset to skip records for the next page.)
        // For simplicity, I am assuming a limit of 10
        Integer offset = fetchGroceriesRequest.getPageNumber() * limit; // Offset calculation

        ResponseBeanGrocery response = new ResponseBeanGrocery();
        List<TdGroceryItems> groceryList;

        // If a name is specified in the request, fetch the record with that name
        if(fetchGroceriesRequest.getName()!=null){
            try {
                groceryList = tdGroceryItemDao.findByName(fetchGroceriesRequest.getName());
            } catch (Exception e) {
                logger.error("Error occurred while fetching groceries from DB: {}", e.getMessage(), e);
                response.setStatus("Failed");
                response.setStatusCode(500);
                response.setStatusMsg("Error occurred while retrieving groceries using name from DB");
                return response;
            }}
        else{
            try{
                groceryList = tdGroceryItemDao.findAllGroceries(limit, offset);} // If no name is specified, fetch all records sorted by name.
            catch (Exception e){
                logger.error("Error occurred while retrieving groceries from DB: {}", e.getMessage(), e);
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
        response.setStatusMsg("Groceries fetched successfully");
        response.setResult(groceryResponseList);
        logger.info("Groceries fetched successfully");
        return response;
    }

    @Override
    @CacheEvict(value = {"allGroceries", "availableGroceries"}, allEntries = true)
    public ResponseBean removeGroceryFromSystem(RemoveGroceryRequest removeGroceryRequest) {
        logger.info("Entering removeGroceryFromSystem :: AdminServiceImpl with ID: {}", removeGroceryRequest.getId());
        ResponseBean response = new ResponseBean();

        try{
        Optional<TdGroceryItems> groceryItem = tdGroceryItemDao.findById(removeGroceryRequest.getId());

        if (!groceryItem.isPresent()) {
            response.setStatus("Failure");
            response.setStatusMsg("Grocery item not found!");
            response.setStatusCode(404);
            logger.error("Grocery item with ID {} not found!", removeGroceryRequest.getId());
            return response;
        }
            // Soft deleting the grocery item by updating its status to "Not Available"
            // instead of permanently deleting it, to maintain data consistency.
            TdGroceryItems grocery = groceryItem.get();
            grocery.setStatus("NotAvailable");
            grocery.setModificationDate(new Timestamp(System.currentTimeMillis()));
            tdGroceryItemDao.save(grocery);

            response.setStatus("Success");
            response.setStatusMsg("Grocery item removed successfully from system!");
            response.setStatusCode(200);
            logger.info("Grocery item with ID {} marked as Not Available.", removeGroceryRequest.getId());
        } catch (Exception e) {
            logger.error("Error occurred while removing grocery item with ID {}: {}", removeGroceryRequest.getId(), e.getMessage(), e);
            response.setStatus("Failure");
            response.setStatusMsg("An error occurred while removing the grocery item. Please try again.");
            response.setStatusCode(500);
        }
        return response;
    }

    @Override
    @CacheEvict(value = {"allGroceries",
            "availableGroceries",
            "orderDetails"}, allEntries = true)
    public ResponseBean clearCache(){

        logger.info("entering clearCache :: AdminServiceIMPL");

        ResponseBean response = new ResponseBean();
        response.setStatus("Success");
        response.setStatusMsg("Cache cleared successfully!");
        response.setStatusCode(200);
        return response;
    }
}
