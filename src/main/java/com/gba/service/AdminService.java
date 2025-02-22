package com.gba.service;

import com.gba.beans.*;
import org.springframework.cache.annotation.CacheEvict;

public interface AdminService {

    ResponseBean addOrUpdateGroceryItem(AddGroceryRequest addGroceryRequest);

    // API to fetch all grocery items from the system
    ResponseBeanGrocery fetchGroceries(FetchGroceriesRequest fetchGroceriesRequest);

    ResponseBean removeGroceryFromSystem(RemoveGroceryRequest removeGroceryRequest);

    @CacheEvict(value = {"allGroceries",
            "availableGroceries",
            "orderDetails"}, allEntries = true)
    ResponseBean clearCache();
}
