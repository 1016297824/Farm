package com.farm.repository;

import com.farm.entity.Customer;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CustomizedRepository<Customer, Integer> {

    @Query("from Customer cus")
    List<Customer> findAll();

    @Query("select cus from Customer cus where cus.username=:username ")
    Customer findByUsername(@Param("username") String username);
}
