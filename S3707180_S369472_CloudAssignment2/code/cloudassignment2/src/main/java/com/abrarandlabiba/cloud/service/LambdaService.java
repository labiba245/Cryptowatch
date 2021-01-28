package com.abrarandlabiba.cloud.service;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abrarandlabiba.cloud.data.SMSRequest;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.util.json.Jackson;;

@Service
public class LambdaService implements Serializable{

	private static final long serialVersionUID = 2419213404295329718L;

	final Logger logger = LoggerFactory.getLogger(LambdaService.class);
	
    AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
//    		.withCredentials(new ProfileCredentialsProvider("default"))
    		.withRegion(Regions.US_EAST_1)
    		.build();
    
    public static final String FUNCTION_NAME = "arn:aws:lambda:us-east-1:720282595548:function:CloudAssignment2_lambda";
    
	public String callSMSFunction(List<SMSRequest> sMSRequests) {
 
		String retMsg = "";
		
		String payload = Jackson.toJsonPrettyString(sMSRequests) ;
		logger.info("payload:\n{} ", payload);
		
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(FUNCTION_NAME)
                .withPayload(payload);
        
		InvokeResult invokeResult = awsLambda.invoke(invokeRequest);

		retMsg = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

        //write out the return value
        System.out.println(retMsg);

        return retMsg;
	}
}