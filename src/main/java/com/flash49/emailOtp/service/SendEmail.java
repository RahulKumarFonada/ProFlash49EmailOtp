package com.flash49.emailOtp.service;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.flash49.emailOtp.entity.SendEmailOtpLogs;
import com.flash49.emailOtp.repository.LogsRepository;

/**
 * 
 * @author RahulRajput
 *
 */
@Service
public class SendEmail {

	@Value("${spring.mail.username}")
	String userMail;

	@Value("${baseUrl.send.Otp.onMobile}")
	private String otpSendApi;
	@Value("${send.Otp.username}")
	private String otpUsername;
	@Value("${send.Otp.password}")
	private String otpPassword;
	@Value("${send.Otp.unicode}")
	private String otpUnicode;
	@Value("${send.Otp.from}")
	private String otpFrom;
	@Value("${send.Otp.dltContentId}")
	private String otpDltContentId;
	

	@Autowired
	public JavaMailSender emailSender;

	@Autowired
	private LogsRepository logRepository;
	public static final Logger Logger = LoggerFactory.getLogger(SendEmail.class);

	/**
	 * 
	 * @param otpMsg
	 * @param mailList
	 * @return
	 */
	public String sendEmailForFlash49(String otpMsg, String mailList) {
		Logger.info("Ready For Sending Email From SendEmail.sendEmailForFlash49() ::" + mailList);

		List<String> toList = null;
		String status = "";
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, CharEncoding.UTF_8);

			toList = Arrays.asList(mailList.split(","));
			InternetAddress[] toAddress = new InternetAddress[toList.size()];

			int count1 = 0;

			for (String mailTo : toList) {
				Logger.info("Address To:: " + mailTo);
				toAddress[count1] = new InternetAddress(mailTo);

				count1++;

			}
			message.setRecipients(Message.RecipientType.TO, toAddress);

			helper.setFrom(userMail);

			helper.setText("Hi User," + "\n\n"+ "Please Collect OTP." + "\n\n" + otpMsg + "\n\n"
					+ "Thanks & Regards.");
			helper.setSubject("OTP From pro.Flash49.com Panel");

			emailSender.send(message);
			status = "Email Sent.";
			Logger.info("Status After Sent Email  From SendEmail.sendEmailForFlash49() :: " + status);

		} catch (Exception e) {
			Logger.info("*****Got Exception From SendEmail.sendEmailForFlash49() ***** " + e.getMessage());
			status = "Email Couldn't Send.";
		}
		return status;
	}

	/**
	 * 
	 * @param mobileNo
	 * @param optMsg
	 */
	public String sendOTPByMno(String mobileNo, String optMsg) {
		Logger.info("Ready For Sending SMS From SendEmail.sendOTPByMno() ::" + mobileNo + " And OTP MSG " + optMsg);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			headers.set("Accept", "application/json");
			Logger.info("Genearting Request For Send OTP");
			String urlTemplate = UriComponentsBuilder.fromHttpUrl(otpSendApi).queryParam("username", otpUsername)
					.queryParam("password", otpPassword).queryParam("unicode", otpUnicode).queryParam("from", otpFrom)
					.queryParam("to", mobileNo).queryParam("dltContentId", otpDltContentId)
					.queryParam("text", URLEncoder.encode(optMsg, "UTF-8")).encode()
					.toUriString();

			Logger.info("Sending  Request For SMS:: " + urlTemplate);
			response = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, String.class);

			Logger.info("Response From SMS API::" + response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("Exception in NetClientGet:- " + e);
		}
		return response.getBody();

	}

}
