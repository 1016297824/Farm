package com.farm.repository;

import com.farm.entity.DiningTable;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiningTableRepository extends CustomizedRepository<DiningTable, Integer> {

    @Query("from DiningTable dt")
    List<DiningTable> findAll();

    @Query("select dt from DiningTable dt where dt.id=:id")
    DiningTable findById(@Param("id") int id);
}
