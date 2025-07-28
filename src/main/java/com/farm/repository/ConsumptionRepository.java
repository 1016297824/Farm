package com.farm.repository;

import com.farm.entity.Consumption;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumptionRepository extends CustomizedRepository<Consumption,Integer> {


}
