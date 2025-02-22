# qp-assessment
Grocery Booking API

## Overview :
This is a Spring Boot-based REST API for a grocery booking system. It supports two roles: Admin and User. The Admin can add and update groceries in the system (inventory), while Users can browse groceries, place orders, and view order details.


## Tech Stack
- **Backend:** Java, Spring Boot
- **API Documentation:** Swagger
- **Database:** MySQL
- **Tools:** IntelliJ, Postman, Git, MySQL workbench

## Installation & Setup
- **Prerequisites:**
Java 17+
Maven
MySQL
Postman (for testing APIs)

## DB setup
To set up the database for this project, follow these steps:
- **Schemas of All Tables Used in the Project:**
This project uses the following tables:
Note: Indexing has been implemented to optimize API performance.

1. td_grocery_items
this table stores grocery products and acts as an inventory table. It contains details such as item name, price, available stock, and status.

DDL to create the table :
CREATE TABLE `td_grocery_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `available_quantity` int DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `creation_date` timestamp NULL DEFAULT NULL,
  `modification_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_grocery_name_status` (`name`,`status`),
  KEY `idx_grocery_status_name` (`status`,`name`)
)

2. td_order_map
The td_order_map table stores order details for users.
Each order is linked to a user_id and can contain multiple grocery items.

DDL to create the table:
CREATE TABLE `td_order_map` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `total_price` decimal(10,2) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `creation_date` timestamp NULL DEFAULT NULL,
  `modification_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId_fk_idx` (`user_id`),
  CONSTRAINT `userId_fk` FOREIGN KEY (`user_id`) REFERENCES `td_user_table` (`id`)
)

3. td_order_details_map
The td_order_details_map table stores which grocery items belong to an order.
Since an order can contain multiple grocery items, this table acts as a join table between td_order_map and td_grocery_items.

DDL to create the table:
CREATE TABLE `td_order_details_map` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `grocery_item_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `creation_date` timestamp NULL DEFAULT NULL,
  `modification_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `orderId_fk_idx` (`order_id`),
  KEY `grocery_itemId_fk_idx` (`grocery_item_id`),
  CONSTRAINT `grocery_itemId_fk` FOREIGN KEY (`grocery_item_id`) REFERENCES `td_grocery_items` (`id`),
  CONSTRAINT `orderId_fk` FOREIGN KEY (`order_id`) REFERENCES `td_order_map` (`id`)
)

4. td_user_table
The td_user_table stores user details.
This table is optional and added to map users to orders.

DDL to create the table:
CREATE TABLE `td_user_table` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `mobile_number` varchar(10) NOT NULL,
  `email_id` varchar(100) DEFAULT NULL,
  `role` varchar(45) DEFAULT 'User',
  `status` varchar(45) DEFAULT NULL,
  `creation_date` timestamp NULL DEFAULT NULL,
  `modification_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mobile_number_UNIQUE` (`mobile_number`)
)

## API Documentation
- **I have also implemented Swagger for API documentation in this project.**
After setting up and running this project on your local machine, you can access the Swagger documentation using the following URL:
"http://localhost:PORT/gba/swagger-ui/index.html#/"

# APIs in this Project with Request and Response Examples:
1. Create User API
Endpoint: http://localhost:PORT/gba/user/createUser
Description: Temporary API to create a user (not required in the assessment but added to populate master data in the system).

Example Request & Response (from Postman):
Request Body : {
    "name" : "Prajyot Patil",
    "mobileNumber" : "8090778891",
    "emailId" : "prajyot@gmail.com"
}
Response: {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "User created successfully!"
}

**CuRL for createUser:** 
curl --location 'localhost:8099/gba/user/createUser' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=571A4D10B790C68A9631FA3876C488BE' \
--data-raw '{
    "name" : "Adam John",
    "mobileNumber" : "8198761890",
    "emailId" : "adam@gmail.com"
}'

// ADMIN APIs
2. Add new or update exisiting grocery item in the system (inventory)
Endpoint: http://localhost:8099/gba/admin/addOrUpdateGroceryItem
Description : This API allows adding or updating a grocery item in the system.
- If updateFlag is true, it updates the item based on the provided id.
- If updateFlag is false, it adds a new grocery item.
- The API also manages inventory (stock levels) by allowing the admin to update the quantity of a grocery item.

Example Request & Response (from Postman):
- i. Create a New Grocery Item
Request Body : {
    "name" : "Sugar",
    "price" : 59,
    "availableQuantity" : "30",
    "updateFlag" : "false"
}
Response : {
    "status": "success",
    "statusCode": 200,
    "statusMsg": "New grocery item added successfully!"
}

- ii. Update an Existing Grocery Item
Request Body :
{
    "id" : 1,
    "name" : "Sugar",
    "price" : 60,
    "availableQuantity" : "30",
    "updateFlag" : "true"
}
Response : {
    "status": "success",
    "statusCode": 200,
    "statusMsg": "Grocery item updated successfully!"
}

- iii. Update Stock Level (Modify Inventory Quantity)
{
    "id" : 1,
    "name" : "Sugar",
    "price" : 60,
    "availableQuantity" : "20",
    "updateFlag" : "true"
}
Response :{
    "status": "success",
    "statusCode": 200,
    "statusMsg": "Grocery item updated successfully!"
}

**CuRL for addOrUpdateGroceryItem**
curl --location 'localhost:8099/gba/admin/addOrUpdateGroceryItem' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=571A4D10B790C68A9631FA3876C488BE' \
--data '{
    
    "name" : "Oil",
    "price" : 239.99,
    "availableQuantity" : "50",
    "updateFlag" : "false"
}'


3. Fetch All Groceries from the System (Admin Only)
Endpoint : http://localhost:8099/gba/admin/fetchGroceries
Description :
- This API allows the admin to fetch existing grocery items.
- The admin can search for a grocery item by name.
- If no name is specified, all grocery items will be returned with pagination (10 records per page).
- Results are sorted alphabetically, and indexing is implemented for better performance.

Example Request & Response (from Postman):
- i. Fetch groceries by name
Request body : {
    "name" : "sugar",
    "pageNumber" : 0
}

Response: {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Groceries fetched successfully",
    "result": [
        {
            "id": 1,
            "name": "Sugar",
            "price": 60.00,
            "availableQuantity": 20,
            "status": "Available",
            "creationDate": "2025-02-22T09:17:54.000+00:00",
            "modificationDate": "2025-02-22T09:22:52.000+00:00"
        }
    ]
}

- ii. Fetch All Grocery Items (With Pagination & Sorting)
Request body : {
    "pageNumber" : 0
}

Response : {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Groceries fetched successfully",
    "result": [
        {
            "id": 2,
            "name": "Biscuit",
            "price": 20.00,
            "availableQuantity": 120,
            "status": "Available",
            "creationDate": "2025-02-22T09:30:40.000+00:00",
            "modificationDate": "2025-02-22T09:30:40.000+00:00"
        },
        {
            "id": 4,
            "name": "Butter",
            "price": 89.99,
            "availableQuantity": 78,
            "status": "Available",
            "creationDate": "2025-02-22T09:31:34.000+00:00",
            "modificationDate": "2025-02-22T09:31:34.000+00:00"
        },
        {
            "id": 3,
            "name": "Rice",
            "price": 799.99,
            "availableQuantity": 100,
            "status": "Available",
            "creationDate": "2025-02-22T09:31:09.000+00:00",
            "modificationDate": "2025-02-22T09:31:09.000+00:00"
        },
        {
            "id": 1,
            "name": "Sugar",
            "price": 60.00,
            "availableQuantity": 20,
            "status": "Available",
            "creationDate": "2025-02-22T09:17:54.000+00:00",
            "modificationDate": "2025-02-22T09:22:52.000+00:00"
        }
    ]
}

**CuRL for fetchGroceries:**
curl --location 'localhost:8099/gba/admin/fetchGroceries' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=571A4D10B790C68A9631FA3876C488BE' \
--data '{
    "name" : "sugar",
    "pageNumber" : 0
}'

4. Remove Grocery Item from the System (Admin Only)
Endpoint : http://localhost:8099/gba/admin/removeGroceryFromSystem
description : This API soft deletes a grocery item by updating its status to "NotAvailable" instead of permanently deleting it.
This ensures data consistency and allows for future recovery if needed.

Example Request & Response (from Postman):
Request body : {
    "id" : 2
}

Response : {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Grocery item removed successfully from system!"
}

**CuRL for removeGroceryFromSystem**
curl --location 'localhost:8099/gba/admin/removeGroceryFromSystem' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=571A4D10B790C68A9631FA3876C488BE' \
--data '{
    "id" : 2
}'


// USER APIs
1. Fetch All Available Groceries from Inventory (For User)
Endpoint : http://localhost:8099/gba/user/fetchAvailableGroceries
description : API to fetch available groceries for users.
- Items marked as "Not Available" by the admin are excluded from the results.
- Users can search by grocery name or fetch all available groceries.
- Pagination (10 records per page) is implemented.
- Results are sorted alphabetically, with indexing for better performance

Example Request & Response (from Postman):
- i. Fetch available groceries by name
Request body : {
    "name" : "Rice",
    "pageNumber" : 0
}

Response : {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Available Groceries fetched successfully",
    "result": [
        {
            "id": 3,
            "name": "Rice",
            "price": 799.99,
            "availableQuantity": 100,
            "status": "Available",
            "creationDate": "2025-02-22T09:31:09.000+00:00",
            "modificationDate": "2025-02-22T09:31:09.000+00:00"
        }
    ]
}

- ii. Fetch All available Grocery Items (With Pagination & Sorting)
Request body : {
    "pageNumber" : 0
}

Response : {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Available Groceries fetched successfully",
    "result": [
        {
            "id": 4,
            "name": "Butter",
            "price": 89.99,
            "availableQuantity": 78,
            "status": "Available",
            "creationDate": "2025-02-22T09:31:34.000+00:00",
            "modificationDate": "2025-02-22T09:31:34.000+00:00"
        },
        {
            "id": 3,
            "name": "Rice",
            "price": 799.99,
            "availableQuantity": 100,
            "status": "Available",
            "creationDate": "2025-02-22T09:31:09.000+00:00",
            "modificationDate": "2025-02-22T09:31:09.000+00:00"
        },
        {
            "id": 1,
            "name": "Sugar",
            "price": 60.00,
            "availableQuantity": 20,
            "status": "Available",
            "creationDate": "2025-02-22T09:17:54.000+00:00",
            "modificationDate": "2025-02-22T09:22:52.000+00:00"
        }
    ]
}

**CuRL for fetchAvailableGroceries**
curl --location 'localhost:8099/gba/user/fetchAvailableGroceries' \
--header 'Content-Type: application/json' \
--data '{
    
    "pageNumber" : 0
}'


2. Create (Book) Order for User Including Multiple Items
Endpoint : http://localhost:8099/gba/user/createOrder
description : This API allows users to place an order with multiple grocery items.
- The system validates stock availability before placing the order.
- If any item is out of stock or requested quantity exceeds stock, an error response is returned.
- If all items are available, the order is placed successfully, and the requested quantity is deducted from inventory.
- Since payment logic is not implemented, I have kept the order status as "Pending".

Example Request & Response (from Postman):
- i. Create (Book) Order - When Stock is Available
Request body: {
  "userId": 1,
  "items": [
    { "itemId": 1, "quantity": 5 },
    { "itemId": 3, "quantity": 1 }
  ]
}

Response : {
    "status": "success",
    "statusCode": 200,
    "statusMsg": "Order placed successfully!",
    "result": {
        "orderId": 1,
        "status": "Pending"
    }
}

- ii. Create (Book) Order - When Requested Quantity is Not Available
Request body: {
  "userId": 1,
  "items": [
    { "itemId": 1, "quantity": 51 },
    { "itemId": 3, "quantity": 1 }
  ]
}

Response: {
    "status": "failure",
    "statusCode": 400,
    "statusMsg": "Insufficient stock for item ID: 1",
    "result": null
}

**CuRL for createOrder**
curl --location 'localhost:8099/gba/user/createOrder' \
--header 'Content-Type: application/json' \
--data '{
  "userId": 1,
  "items": [
    { "itemId": 4, "quantity": 1 },
    { "itemId": 3, "quantity": 2 }
  ]
}'

3. Fetch Order Details for User
Endpoint : http://localhost:8099/gba/user/fetchOrderDetails
description : This api retrieves the order detail for the specific user.
- If orderId is specified - Fetch details of that particular order.
- If orderId is not specified - Fetch all orders of that user.
- Pagination is implemented (10 orders per page, sorted from most recent).

Example Request & Response (from Postman):
- i. Fetch Specific Order Details (when orderId is specified)
Request body: {
    "userId" : 1,
    "orderId" : 1,
    "pageNumber" : 0
}

Response: {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Fetched successfully",
    "result": [
        {
            "orderId": 1,
            "userId": 1,
            "totalPrice": 1099.99,
            "status": "Pending",
            "items": [
                {
                    "itemId": 1,
                    "name": "Sugar",
                    "price": 300.00,
                    "quantity": 5
                },
                {
                    "itemId": 3,
                    "name": "Rice",
                    "price": 799.99,
                    "quantity": 1
                }
            ]
        }
    ]
}

- ii. Fetch All Orders for User (Order History)
Request body;{
    "userId" : 1,
    "pageNumber" : 0
}

Response: {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Fetched successfully",
    "result": [
        {
            "orderId": 2,
            "userId": 1,
            "totalPrice": 6189.47,
            "status": "Pending",
            "items": [
                {
                    "itemId": 4,
                    "name": "Butter",
                    "price": 4589.49,
                    "quantity": 51
                },
                {
                    "itemId": 3,
                    "name": "Rice",
                    "price": 1599.98,
                    "quantity": 2
                }
            ]
        },
        {
            "orderId": 1,
            "userId": 1,
            "totalPrice": 1099.99,
            "status": "Pending",
            "items": [
                {
                    "itemId": 1,
                    "name": "Sugar",
                    "price": 300.00,
                    "quantity": 5
                },
                {
                    "itemId": 3,
                    "name": "Rice",
                    "price": 799.99,
                    "quantity": 1
                }
            ]
        }
    ]
}

**CuRL for fetchOrderDetails**
curl --location 'localhost:8099/gba/user/fetchOrderDetails' \
--header 'Content-Type: application/json' \
--data '{
    "userId" : 1,
    
    "pageNumber" : 0
}'

## API to clear cache once every day
Endpoint : GET http://localhost:8099/gba/cache/refreshCache
description : This API is used to clear the cache once every day.
- - you can manually call this api to clear all the caches when you need or internally a schedular is used to trigger this api once every 24 hr at 2 am to automatically clear the cache
No request parameters are required.

Example Request & Response (from Postman):
Response: {
    "status": "Success",
    "statusCode": 200,
    "statusMsg": "Cache cleared successfully!"
}

**CuRL for refreshCache**
curl --location 'localhost:8099/gba/cache/refreshCache'


# Contact
- **For any queries, reach out at: patilprajyot008@gmail.com**

