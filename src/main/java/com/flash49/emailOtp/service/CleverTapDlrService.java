package com.flash49.emailOtp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flash49.emailOtp.controller.SendEmailOtpController;
import com.flash49.emailOtp.entity.CleverTapEntity;
import com.flash49.emailOtp.repository.CleverTapRepository;

@Service
public class CleverTapDlrService {

	@Autowired
	private CleverTapRepository cleverTapRepository;
	public static final Logger Logger = LoggerFactory.getLogger(SendEmailOtpController.class);

	public void sendCleverTapDlr(String username, String password, String from, String dltContentId,
			String corelationid, String to, String text, String unicode, String param, String entityid,
			String deliverystatus) {
		Logger.info("***** Going To Saving cleverTab dlr *****");
		CleverTapEntity cleverTapEntity = new CleverTapEntity();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			cleverTapEntity.setCorelationid(corelationid);
			cleverTapEntity.setSentDlr(0);
			cleverTapEntity.setDeliverystatus(deliverystatus);
			cleverTapEntity.setDltContentId(dltContentId);
			cleverTapEntity.setEntityid(entityid);
			cleverTapEntity.setFrom(from);
			cleverTapEntity.setParam(param);
			cleverTapEntity.setPassword(password);
			cleverTapEntity.setText(text);
			cleverTapEntity.setTo(to);
			cleverTapEntity.setUnicode(unicode);
			cleverTapEntity.setUsername(username);
			cleverTapEntity.setCreatedDate(dateFormat.format(new Date()));
			cleverTapRepository.save(cleverTapEntity);

			Logger.info("***** After  Saving cleverTab dlr *****" + cleverTapEntity.toString());
		} catch (Exception e) {
			Logger.info("***** After  Saving cleverTab dlr *****" + cleverTapEntity.toString());

			e.printStackTrace();
		}
	}

	

}
