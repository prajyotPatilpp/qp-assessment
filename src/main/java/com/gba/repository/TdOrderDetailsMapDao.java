package com.gba.repository;

import com.gba.entity.TdGroceryItems;
import com.gba.entity.TdOrderDetailsMap;
import com.gba.entity.TdOrderMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Repository
public interface TdOrderDetailsMapDao extends JpaRepository<TdOrderDetailsMap, Integer> {

    @Query("SELECT sum(m.price) FROM TdOrderDetailsMap m WHERE m.orderId = :orderId")
    BigDecimal findTotalPriceByOrderId(Integer orderId);

    @Query("SELECT m FROM TdOrderDetailsMap m WHERE m.orderId = :orderId")
    List<TdOrderDetailsMap> findByOrderId(Integer orderId);



}
