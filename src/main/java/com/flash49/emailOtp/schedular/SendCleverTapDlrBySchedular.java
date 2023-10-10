package com.flash49.emailOtp.schedular;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.flash49.emailOtp.entity.CleverTapEntity;
import com.flash49.emailOtp.repository.CleverTapRepository;

@Service
public class SendCleverTapDlrBySchedular {
	public static final Logger Logger = LoggerFactory.getLogger(SendCleverTapDlrBySchedular.class);

	@Value("${cleverTap.callback.url}")
	String cleverTapCallBackUrl;
	@Autowired
	private CleverTapRepository cleverTapRepository;

	@Scheduled(cron = "0 0/1 * * * *")
	public void sendCleverTapDlr() throws JSONException, ParseException {
		Logger.info("***** sendCleverTapDlr Schedular Started *****");
		List<CleverTapEntity> cleverTapEntity = null;
		cleverTapEntity = cleverTapRepository.findBySentDrl();
		synchronized (this) {

			if (cleverTapEntity.size() > 0) {
				Logger.info("***** sendCleverTapDlr Schedular Got cleverTap Size *****::" + cleverTapEntity.size());

				for (CleverTapEntity c : cleverTapEntity) {
					JSONArray cleverTapRoot = new JSONArray();
					JSONObject cleverTap = new JSONObject();

					JSONObject cleverTapRootRequest = new JSONObject();
					cleverTapRootRequest.put("event", c.getDeliverystatus());
					JSONObject data = new JSONObject();
					data.put("ts", convertDateTimeToEpochTime(c.getCreatedDate()));
					data.put("meta", c.getCorelationid());
					if (c.getDeliverystatus().contains("FAILED")) {
						data.put("code", 900);
					}
					data.put("description", c.getDeliverystatus());
					JSONArray list = new JSONArray();
					list.put(data);
					cleverTapRootRequest.put("data", list);
					cleverTapRoot.put(cleverTapRootRequest);

					cleverTap.put("cleverTap", cleverTapRoot);
					String response = sendJsonObjectToCleverTapAPI(cleverTapRoot);
					if (response.contains("success")) {
						cleverTapRepository.updateById("1", c.getId());
						Logger.info("***** sendCleverTapDlr Schedular Got Success Response *****::" + response);

					} else {
						cleverTapRepository.updateById("2", c.getId());
						Logger.info("***** sendCleverTapDlr Schedular Got Failed Response *****::" + response);

					}
				}
			}
		}
		Logger.info("***** sendCleverTapDlr Schedular Ended *****");

	}

	public long convertDateTimeToEpochTime(String dateString) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = df.parse(dateString);
		long time = date.getTime();
		return time;
	}

	public String sendJsonObjectToCleverTapAPI(JSONArray cleverTapRootRequest) {
		String output = "cleverTapCallBackUrl";
		try {

			URL url = new URL(cleverTapCallBackUrl);
			System.out.println("URL used for sendJsonObjectToCleverTapAPI is ::" + url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			os.write(cleverTapRootRequest.toString().getBytes());
			os.flush();
			if (conn.getResponseCode() != 200) {

				System.out.println("Error Response Got From sendJsonObjectToCleverTapAPI  URI Service.");

				InputStreamReader in = new InputStreamReader(conn.getErrorStream());
				BufferedReader br = new BufferedReader(in);
				output = "";
				String outputloop = "";
				while ((outputloop = br.readLine()) != null) {
					output = output + outputloop;
					// System.out.println("JSON is :::" + output);

				}
				JSONObject responseObj = new JSONObject(output);
				JSONObject response = responseObj.getJSONObject("response");
				String errors = response.optString("errors");

				System.out.println("Error Message From sendJsonObjectToCleverTapAPI API" + errors);
				return output;
			} else {
				System.out.println("Preparing Request for Sending To sendJsonObjectToCleverTapAPI API");
				InputStreamReader in = new InputStreamReader(conn.getInputStream());
				BufferedReader br = new BufferedReader(in);
				output = "";
				String outputloop = "";
				while ((outputloop = br.readLine()) != null) {
					output = output + outputloop;
					// System.out.println("JSON is :::" + output);

				}
			}
			conn.disconnect();

		} catch (IOException | RuntimeException e) {
			System.out.println("Exception sendJsonObjectToCleverTapAPI from in NetClientGet:- " + e);
		}
		return output;
	}
}
