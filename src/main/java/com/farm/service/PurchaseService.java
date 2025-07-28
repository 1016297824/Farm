package com.farm.service;

import com.farm.entity.Purchase;
import com.farm.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    // 保存采购信息
    public void save(Purchase purchase) {

        purchaseRepository.save(purchase);
    }

    // 通过时间查询采购信息
    public List<Purchase> findByTime(LocalDateTime startTime, LocalDateTime endTime) {

        return purchaseRepository.findByTime(startTime, endTime);
    }
}
