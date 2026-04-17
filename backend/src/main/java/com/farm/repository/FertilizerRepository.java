package com.farm.repository;

import com.farm.entity.Fertilizer;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FertilizerRepository extends CustomizedRepository<Fertilizer, Integer> {

    @Query("from Fertilizer ")
    List<Fertilizer> findAll();

    @Query("select f from Fertilizer f where f.fertilizerType=:fertilizerType")
    List<Fertilizer> findByFertilizerType(@Param("fertilizerType") String fertilizerType);

    @Query("select f from Fertilizer f where f.name=:name")
    Fertilizer findByName(@Param("name") String name);
}
