package com.abrarandlabiba.cloud.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abrarandlabiba.cloud.data.SMSRequest;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;;

public class SMSService implements Serializable {

	private static final long serialVersionUID = -3711548510155842308L;

	final Logger logger = LoggerFactory.getLogger(SMSService.class);

	static AmazonSNS localSNSclient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1)
//		    .withCredentials(new ProfileCredentialsProvider("default"))
			.build();

	public String sendSMSNotifications(List<SMSRequest> smsRequests) {
		String retMsg = "";
		Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();

		int countSuccess = 0;
		try {
			String sendSmsStr = System.getenv("sendSms");
			logger.info("sendSms environment variable is set to :'" + sendSmsStr + "'");

			if ("false".equals(sendSmsStr)) {
				logger.info("sendSms environment variable is set to :'" + sendSmsStr + "' so no SMS will be sent.");
			} else {

				for (SMSRequest smsRequest : smsRequests) {
					PublishResult pr = localSNSclient.publish(new PublishRequest().withMessage(smsRequest.getMessage())
							.withPhoneNumber(smsRequest.getPhoneNumber()).withMessageAttributes(smsAttributes));
					++countSuccess;
					logger.info("SdkResponseMetadata:{}", pr.getSdkResponseMetadata());
				}
			}
		} catch (Exception e) {
			retMsg += "Err: Problem in sending SMS' " + e.getMessage();
			logger.error(e.getMessage());
		}
		finally {
			retMsg += "SMS sent to " + countSuccess + "/" + smsRequests.size() + " recipient(s)";
		}
		return retMsg;
	}

}