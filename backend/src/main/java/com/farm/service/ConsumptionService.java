package com.farm.service;

import com.farm.entity.Consumption;
import com.farm.repository.ConsumptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ConsumptionService {

    @Autowired
    private ConsumptionRepository consumptionRepository;

    // 保存消耗信息
    public void save(Consumption consumption) {

        consumptionRepository.save(consumption);
    }
}
