package com.gba.repository;

import com.gba.entity.TdOrderMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TdOrderMapDao extends JpaRepository<TdOrderMap, Integer> {

    @Query("SELECT m FROM TdOrderMap m WHERE m.id = :orderId AND m.userId = :userId")
    List<TdOrderMap> findByOrderId(Integer orderId, Integer userId);

    @Query(value = "SELECT * FROM td_order_map WHERE user_id = :userId order by id desc LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<TdOrderMap> findAllOrdersForUser(Integer userId, Integer limit, Integer offset);

}
