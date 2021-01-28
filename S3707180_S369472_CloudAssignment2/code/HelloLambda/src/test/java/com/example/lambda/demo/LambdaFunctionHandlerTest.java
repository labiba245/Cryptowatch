package com.example.lambda.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.abrarandlabiba.cloud.data.SMSRequest;
import com.abrarandlabiba.cloud.lambda.LambdaFunctionHandler;
import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {

    private static List<SMSRequest> smsRequests = new ArrayList<>();

    @BeforeClass
    public static void createInput() throws IOException {
    	SMSRequest smsRequest = new SMSRequest();
    	smsRequest.setPhoneNumber("+61412068176");
    	smsRequest.setMessage("Abrar Junit test SMS ");
    	smsRequests.add(smsRequest);
    	
    }

    private Context createContext() {
        TestContext ctx = new TestContext();
        ctx.setFunctionName("CloudAssignment2_lambda");
        return ctx;
    }

    @Test
    public void testLambdaFunctionHandler() {
        LambdaFunctionHandler handler = new LambdaFunctionHandler();
        Context ctx = createContext();

        String output = handler.handleRequest(smsRequests, ctx);

		Assert.assertEquals("SMS sent to " + "0/" + smsRequests.size() + " recipient(s)", 
				output );
    }
}
