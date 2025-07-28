package com.farm.repository;

import com.farm.entity.Menu;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends CustomizedRepository<Menu, Integer> {

    @Query("from Menu m")
    List<Menu> findAll();
}
