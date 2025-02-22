package com.gba.repository;

import com.gba.entity.TdUserTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TdUserTableDao extends JpaRepository<TdUserTable, Integer> {

}
