package com.TakeHomeTest.restApi.repository;

import com.TakeHomeTest.restApi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
