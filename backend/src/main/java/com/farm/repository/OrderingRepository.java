package com.farm.repository;

import com.farm.entity.Ordering;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderingRepository extends CustomizedRepository<Ordering, Integer> {

    @Query("select o from Ordering o where o.reserve.no=:no")
    List<Ordering> findByReserveNo(@Param("no") String no);
}
