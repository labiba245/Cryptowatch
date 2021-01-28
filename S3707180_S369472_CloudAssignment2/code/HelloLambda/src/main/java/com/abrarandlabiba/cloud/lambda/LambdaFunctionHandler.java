package com.abrarandlabiba.cloud.lambda;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abrarandlabiba.cloud.data.SMSRequest;
import com.abrarandlabiba.cloud.service.SMSService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.json.Jackson;

public class LambdaFunctionHandler implements RequestHandler<List<SMSRequest>, String> {

	final Logger logger = LoggerFactory.getLogger(LambdaFunctionHandler.class);
	
	SMSService smsService = new SMSService();

	@Override
	public String handleRequest(List<SMSRequest> input, Context context) {
		context.getLogger().log("-------------------------------------------------------------\n");
		context.getLogger().log("------------------------- START Function -------------------\n");
		context.getLogger().log("-------------------------------------------------------------\n");
		
		logger.info("Input:{}, size:{}", Jackson.toJsonPrettyString(input), input.size() );
		
		String output = smsService.sendSMSNotifications(input);
		
		context.getLogger().log(output);
				
		context.getLogger().log("-------------------------------------------------------------\n");
		context.getLogger().log("-------------------------- END Function ---------------------\n");
		context.getLogger().log("-------------------------------------------------------------\n");

		return output;
	}

}
