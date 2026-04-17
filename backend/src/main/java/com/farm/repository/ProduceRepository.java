package com.farm.repository;

import com.farm.entity.Produce;
import com.farm.repository.impl.CustomizedRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduceRepository extends CustomizedRepository<Produce,Integer> {


}
