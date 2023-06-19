package com.flash49.emailOtp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "send_email_otp_logs")
@Data
public class SendEmailOtpLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;	
	private String request;
	private String response;
	//private String sentFrom;
	private String sentTo;
	private String createdDate;
	

}
