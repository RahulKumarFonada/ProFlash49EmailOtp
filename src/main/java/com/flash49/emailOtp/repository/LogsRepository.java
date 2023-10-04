package com.flash49.emailOtp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flash49.emailOtp.entity.SendEmailOtpLogs;

@Repository
public interface LogsRepository extends JpaRepository<SendEmailOtpLogs, Long> {

	@Query(value = "select * from send_email_otp_logs where request like %:text% and sent_to like  %:to% order by id limit 1", nativeQuery = true)
	SendEmailOtpLogs findTopByOrderByIdDescRequestContainingAndSentToContaining(@Param("text") String text,
			@Param("to") String to);
}
