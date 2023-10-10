package com.flash49.emailOtp.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flash49.emailOtp.entity.SendEmailOtpEntity;
import com.flash49.emailOtp.entity.SendEmailOtpLogs;
import com.flash49.emailOtp.repository.LogsRepository;
import com.flash49.emailOtp.repository.SendEmailOtpRepository;
import com.flash49.emailOtp.service.CleverTapDlrService;
import com.flash49.emailOtp.service.SendEmail;

@RestController
@RequestMapping(value = "/fonada/flash49/api/v1")
public class SendEmailOtpController {
	public static final Logger Logger = LoggerFactory.getLogger(SendEmailOtpController.class);

	@Autowired
	private SendEmailOtpRepository sendEmailOtpRepository;

	@Autowired
	private SendEmail sendEmail;

	@Autowired
	private LogsRepository logsRepository;

	@Autowired
	private CleverTapDlrService cleverTapDlrService;

	@SuppressWarnings("null")
	@GetMapping("/sendEmailOtp")
	public ResponseEntity<HashMap<String, Object>> sendEmailOtp(@RequestParam(required = true) String username,
			@RequestParam(required = true) String password, @RequestParam(required = true) String from,
			@RequestParam(required = false) String dltContentId, @RequestParam(required = false) String corelationid,
			@RequestParam(required = true) String to, @RequestParam(required = true) String text,
			@RequestParam(required = true) String unicode, @RequestParam(required = false) String param,
			@RequestParam(required = false) String entityid, @RequestParam(required = false) String deliverystatus)
			throws Exception {
		SendEmailOtpEntity sendEmailOtp = null;
		HashMap<String, Object> result = new HashMap<>();
		String otpResponse = "";

		try {
			if (Objects.nonNull(corelationid) && !corelationid.isEmpty() && !corelationid.equalsIgnoreCase("")) {
				Logger.info(
						" Inside SendEmailOtpController.sendEmailOtp() Request with corelation Id::" + corelationid);

				cleverTapDlrService.sendCleverTapDlr(username, password, from, dltContentId, corelationid, to, text,
						unicode, param, entityid, deliverystatus);
				result.put("STATUS", 200);
				result.put("MESSAGE", "Success");
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
			if (Objects.isNull(param) && Objects.isNull(corelationid)) {
				Logger.info(" Inside SendEmailOtpController.sendEmailOtp() for sending OTP ::");

				if (Objects.isNull(to) || to.equals("") || to.isEmpty()) {
					throw new Exception(" {to} Param Cannot Be Empty Or Null OR Blank.");

				}
				if (Objects.isNull(text) || text.equals("") || text.isEmpty()) {
					throw new Exception(" {text} Param Cannot Be Empty Or Null OR Blank.");

				}
				if (to.length() >= 10 && to.length() <= 12) {
					sendEmailOtp = sendEmailOtpRepository.findByMsisdnContains(trimZeros(to));
					if (logsRepository.findTopByOrderByIdDescRequestContainingAndSentToContaining(text,
							sendEmailOtp.getOtpMsisdn()) == null) {

						if (Objects.nonNull(sendEmailOtp)) {
							return sendEmaiAndOTP(sendEmailOtp, text, to);
						}
					} else {
						saveLogs("Record Already Exits Msg:: " + text + " And Msisdn ::" + to, "Mobile No Not Valid",
								to, to);
						result.put("STATUS", 200);
						result.put("MESSAGE", "Same OTP Not Allowed");
						Logger.info("Record Already Exits Msg:: " + text + " And Msisdn ::" + to, "Mobile No Not Valid",
								to, to);
						return new ResponseEntity<>(result, HttpStatus.OK);

					} /*
						 * else { if (Objects.isNull(sendEmailOtp)) {
						 * Logger.info("Record Doesn't Exist As Given MSISDN For OTP AND E-Mail :: " +
						 * to + " But Send Sms Directly"); otpResponse = sendEmail.sendOTPByMno(to,
						 * text); saveLogs("Record Doesn't Exist As Given MSISDN For OTP AND E-Mail :: "
						 * + to + " But Send Sms Directly" + to, otpResponse, to, to);
						 * result.put("STATUS", 200); result.put("MESSAGE", otpResponse); return new
						 * ResponseEntity<>(result, HttpStatus.OK); } }
						 */
				} else {
					saveLogs("Msg:: " + text + " And Msisdn ::" + to, "Mobile No Not Valid", to, to);
					result.put("STATUS", 200);
					result.put("MESSAGE", "Mobile No Not Valid");
					return new ResponseEntity<>(result, HttpStatus.OK);

				}
			}
		} catch (

		Exception e) {
			saveLogs("Otp Msg:: " + text + " And Msisdn ::" + to, e.getMessage(), to, to);
			e.printStackTrace();
			result.put("STATUS", 200);
			result.put("MESSAGE", e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return null;
	}

	/**
	 * 
	 * @param sendEmailOtp
	 * @param text
	 * @param to
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<HashMap<String, Object>> sendEmaiAndOTP(SendEmailOtpEntity sendEmailOtp, String text,
			String to) throws Exception {
		String emailResponse = "";

		HashMap<String, Object> result = new HashMap<>();
		try {

			if (Objects.isNull(sendEmailOtp.getEmail()) && sendEmailOtp.getEmail().isEmpty()
					&& Objects.isNull(sendEmailOtp.getOtpMsisdn()) && sendEmailOtp.getOtpMsisdn().isEmpty()) {
				throw new Exception("Email or MISDN Cannot Be Empty.");
			}
			/**
			 * For Checking both condition mail and otp
			 */
			if (Objects.nonNull(sendEmailOtp.getEmail()) && !sendEmailOtp.getEmail().isEmpty()
					&& Objects.nonNull(sendEmailOtp.getOtpMsisdn()) && !sendEmailOtp.getOtpMsisdn().isEmpty()) {
				Logger.info("Got Data For Sending Email And OTP :: " + sendEmailOtp.getEmail() + " And "
						+ sendEmailOtp.getOtpMsisdn());

				/**
				 * For Send Email
				 */
				emailResponse = sendEmail.sendEmailForFlash49(text, sendEmailOtp.getEmail());
				saveLogs("Email Msg:: " + text + " And Msisdn ::" + to, emailResponse, to, sendEmailOtp.getEmail());
				/**
				 * For Sending OTP
				 */
				sendOTP(sendEmailOtp, text, to);

				result.put("STATUS", 200);
				result.put("MESSAGE", "Email Sent to " + sendEmailOtp.getEmail() + " And OTP Sent To "
						+ Arrays.asList(sendEmailOtp.getOtpMsisdn().split(",")) + " Successfully.");
				return new ResponseEntity<>(result, HttpStatus.OK);

			}
			/**
			 * condition valid for email send
			 */
			else {
				if (Objects.nonNull(sendEmailOtp.getEmail()) && !sendEmailOtp.getEmail().isEmpty()) {
					Logger.info("Got Data For Sending Email:: " + sendEmailOtp.toString());
					/**
					 * condition valid for sending email
					 */
					emailResponse = sendEmail.sendEmailForFlash49(text, sendEmailOtp.getEmail());
					saveLogs("Email Msg:: " + text + " And Msisdn ::" + to, emailResponse, to, sendEmailOtp.getEmail());

					if (Objects.nonNull(emailResponse)) {
						result.put("STATUS", 200);
						result.put("MESSAGE", "Email Sent to " + sendEmailOtp.getEmail() + " Successfully.");
						return new ResponseEntity<>(result, HttpStatus.OK);
					}

				}
				/**
				 * condition valid for sending otp
				 */
				else if (Objects.nonNull(sendEmailOtp.getOtpMsisdn()) && !sendEmailOtp.getOtpMsisdn().isEmpty()) {
					return sendOTP(sendEmailOtp, text, to);
				}
			}
		} catch (Exception e) {
			saveLogs("Otp Msg:: " + text + " And Msisdn ::" + to, e.getMessage(), to, to);
			e.printStackTrace();
			result.put("STATUS", 200);
			result.put("MESSAGE", e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.OK);

		}

		return null;
	}

	public ResponseEntity<HashMap<String, Object>> sendOTP(SendEmailOtpEntity sendEmailOtp, String text, String to)
			throws Exception {
		String otpResponse = "";
		HashMap<String, Object> result = new HashMap<>();

		Logger.info("Got Data For Sending SMS:: " + sendEmailOtp.toString());
		String msisdnArrayList[] = sendEmailOtp.getOtpMsisdn().split(",");
		List<String> msisdnList = Arrays.asList(msisdnArrayList);
		for (String msisdn : msisdnList) {
			if (msisdn.length() >= 10 && msisdn.length() <= 12) {
				otpResponse = sendEmail.sendOTPByMno(msisdn, text);
				saveLogs("Otp Msg:: " + text + " And Msisdn ::" + to, otpResponse, to, sendEmailOtp.getOtpMsisdn());
			} else {
				Logger.info("Otp Msg:: " + text + " And Msisdn ::" + to, "Mobile No Not Valid.", to,
						sendEmailOtp.getOtpMsisdn());
				saveLogs("Otp Msg:: " + text + " And Msisdn ::" + to, "Mobile No Not Valid.", to,
						sendEmailOtp.getOtpMsisdn());
			}
		}
		if (Objects.nonNull(otpResponse)) {
			result.put("STATUS", 200);
			result.put("MESSAGE", "OTP Sent to " + sendEmailOtp.getOtpMsisdn() + " Successfully.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return null;

	}

	public void saveLogs(String request, String response, String sentFrom, String sendTo) throws Exception {
		SendEmailOtpLogs sendEmailOtpLogs = new SendEmailOtpLogs();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			sendEmailOtpLogs.setRequest(request);
			sendEmailOtpLogs.setResponse(response);
			// sendEmailOtpLogs.setSentFrom(sentFrom);
			sendEmailOtpLogs.setSentTo(sendTo);
			sendEmailOtpLogs.setCreatedDate(dateFormat.format(new Date()));
			logsRepository.save(sendEmailOtpLogs);
		} catch (Exception e) {
			throw new Exception("Exception:: " + e.getMessage());
		}
	}

	public String trimZeros(String number) {

		// trim zeros at left
		number = number.replaceAll("^0*", "");

		// if the length is < 10, returns an empty string
		if (number.length() < 10) {
			return "";
		}

		// if the length is > 10, trims any number at left
		if (number.length() > 10) {
			number = number.replaceAll(".*(\\d{10})$", "$1");
		}

		return number;
	}
}
