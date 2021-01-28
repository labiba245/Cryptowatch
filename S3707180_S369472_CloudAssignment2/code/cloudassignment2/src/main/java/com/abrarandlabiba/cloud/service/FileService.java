package com.abrarandlabiba.cloud.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.binance.api.client.domain.market.TickerStatistics;;

@Service
public class FileService implements Serializable{

	private static final long serialVersionUID = -3711548510155842308L;

	static AmazonS3 s3client = AmazonS3ClientBuilder.standard()
			.withRegion(Regions.US_EAST_1)
//			.withCredentials(new ProfileCredentialsProvider("default"))
			.build();
	
	final static String BUCKET_NAME = "abrarandlabiba-assignment2-bucket";
    final static String STRING_OBJKEY_NAME = "Crypto";
	
	
    static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); 

    public synchronized String putCrypto(List <TickerStatistics> tickers) {
		String retMsg = "";    	
    	StringBuffer sb = new StringBuffer("Added: " + dateFormatter.format(new Date()) + "\n");
    	
    	for (TickerStatistics ticker :tickers) {
    		sb.append(ticker.toString() + "\n");	
    	}
    	

    	try {
    		s3client.putObject(BUCKET_NAME, STRING_OBJKEY_NAME, sb.toString()); 
            retMsg = "Success: " + tickers.size() +  " Crypto tickers updated in " + BUCKET_NAME + " : " + STRING_OBJKEY_NAME;
           
        } catch (Exception e) {
        	retMsg = "Err: String updated in " + BUCKET_NAME + " : " + STRING_OBJKEY_NAME + "' " + e.getMessage();
        }


        System.out.println(retMsg);
    	return retMsg;
    }


}