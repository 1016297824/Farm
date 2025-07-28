package com.farm.service;

import com.farm.entity.Produce;
import com.farm.repository.ProduceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ProduceService {

    @Autowired
    private ProduceRepository produceRepository;

    // 保存生产信息
    public void save(Produce produce) {

        produceRepository.save(produce);
    }
}
