package com.abrarandlabiba.cloud.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;;

@Service
public class TranslationService implements Serializable{

	private static final long serialVersionUID = -3711548510155842308L;


    AmazonTranslate translate = AmazonTranslateClient.builder()
//    	    .withCredentials(new ProfileCredentialsProvider("default"))
		  .withRegion(Regions.US_EAST_1)
            .build();
	
	public String translation (String sentence, String languageCode) {

	    TranslateTextRequest request = new TranslateTextRequest()
	            .withText(sentence) 
	            .withSourceLanguageCode("en")
	            .withTargetLanguageCode(languageCode); //zh  en  ar
	    TranslateTextResult result  = translate.translateText(request);
	    String resultStr = result.getTranslatedText();
	    System.out.println(resultStr);
	    return resultStr;

	}
}