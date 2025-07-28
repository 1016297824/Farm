package com.farm.repository;

import com.farm.entity.RestaurantMaterial;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantMaterialRepository extends CustomizedRepository<RestaurantMaterial, Integer> {

    @Query("from RestaurantMaterial ")
    List<RestaurantMaterial> findAll();

    @Query("select rm from RestaurantMaterial rm where rm.name=:name")
    RestaurantMaterial findByName(@Param("name") String name);
}
