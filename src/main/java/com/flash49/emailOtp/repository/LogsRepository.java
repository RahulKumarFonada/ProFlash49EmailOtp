package com.flash49.emailOtp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flash49.emailOtp.entity.SendEmailOtpLogs;

@Repository
public interface LogsRepository extends JpaRepository<SendEmailOtpLogs, Long> {

}
