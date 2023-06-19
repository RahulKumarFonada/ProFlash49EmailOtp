package com.flash49.emailOtp.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flash49.emailOtp.entity.SendEmailOtpEntity;

@Repository
@Transactional	
public interface SendEmailOtpRepository extends JpaRepository<SendEmailOtpEntity, Integer> {

 SendEmailOtpEntity findByMsisdnContains(String msisdn);
}
