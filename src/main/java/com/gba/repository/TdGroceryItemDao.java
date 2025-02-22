package com.gba.repository;

import com.gba.entity.TdGroceryItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TdGroceryItemDao extends JpaRepository<TdGroceryItems, Integer> {

    @Query("SELECT m FROM TdGroceryItems m WHERE m.name = :name AND m.status = 'Available'")
    List<TdGroceryItems> findByGroceryName(String name);

    List<TdGroceryItems> findByName(String name);

    @Query(value = "SELECT * FROM td_grocery_items order by name LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<TdGroceryItems> findAllGroceries(Integer limit, Integer offset);

    @Query(value = "SELECT * FROM td_grocery_items where status = 'Available' order by name  LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<TdGroceryItems> findAllAvailableGroceries(Integer limit, Integer offset);

    @Query("SELECT m FROM TdGroceryItems m WHERE m.id = :id AND m.status = 'Available'")
    TdGroceryItems findGroceryById(Integer id);

}
