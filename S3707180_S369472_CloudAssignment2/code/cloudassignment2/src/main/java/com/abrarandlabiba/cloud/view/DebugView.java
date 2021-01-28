package com.abrarandlabiba.cloud.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.abrarandlabiba.cloud.data.SMSRequest;
import com.abrarandlabiba.cloud.service.LambdaService;
import com.abrarandlabiba.cloud.service.TranslationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;


@Route("debug")
@CssImport("./styles/shared-styles.css" )
public class DebugView extends VerticalLayout {

	private static final long serialVersionUID = -3702687262495231414L;

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
	public DebugView(@Autowired LambdaService lambdaService, @Autowired TranslationService translationService) {
		
		// Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content"); 

        
		Button toMain_BT = new Button("< Back to Admin Page", e -> UI.getCurrent().getPage().setLocation("/admin"));
        add(toMain_BT);
        
        //---------------- lambda function------------
        
        List<SMSRequest> input = createSMSList();
    	
		Button callLambda_BT = new Button("Call SMS Lambda Sample", e -> lambdaService.callSMSFunction(input));
        add(callLambda_BT);
        
        //---------------- lambda function------------
        
        HorizontalLayout translation_HL = new HorizontalLayout();
        add(translation_HL);
		
		TextField inputSentence_TF = new TextField("Input Setence");
		inputSentence_TF.setValue("Hello World");
		translation_HL.add(inputSentence_TF);
		
		TextField outputSentence_TF = new TextField("Translation");
		translation_HL.add(outputSentence_TF);

		RadioButtonGroup<String> languageِAdmin_RG = new RadioButtonGroup<>();
		translation_HL.add(languageِAdmin_RG);
		languageِAdmin_RG.setLabel("Language");
		languageِAdmin_RG.setItems("Chinese", "Arabic");
		languageِAdmin_RG.setValue("Chinese");

		VerticalLayout translationButton_VL = new VerticalLayout();
		translation_HL.add(translationButton_VL);
		translationButton_VL.add(new Label());
		Button translate_BT = new Button("Translate", e -> outputSentence_TF
				.setValue(translationService.translation(inputSentence_TF.getValue(), languageToCode(languageِAdmin_RG.getValue()))));
		translationButton_VL.add(translate_BT );
		translate_BT.setIcon(new Icon(VaadinIcon.COMMENTS));
        
        
		//------------------------ Debug End --------------------------
        add(new Label());
        add(new Label());
        
        add(new Label("----------------------------------------------------"));
        add(new Label(" Debug Information: "));
        
		TextArea debug_TA = new TextArea();
		Map<String,String> envs = System.getenv();
		
		String buffer;
		buffer = "___________________ Environment Variables _____________________________________";
		System.out.println(buffer);
		debug_TA.setValue(debug_TA.getValue() + buffer + "\n");
		for (String key : envs.keySet()) {
			buffer = "'" + key + "' : '" + envs.get(key) + "'";
			System.out.println(buffer);
			debug_TA.setValue(debug_TA.getValue() + buffer+ "\n");
		}
		buffer = "_____________ System Properties ______________";
		System.out.println(buffer);
		debug_TA.setValue(debug_TA.getValue() + buffer+ "\n");
		Properties props = System.getProperties();
		for (Object key : props.keySet()) {
			buffer = "'" + key + "' : '" + envs.get(key) + "'";
			System.out.println(buffer);
			debug_TA.setValue(debug_TA.getValue() + buffer+ "\n");
		}

		buffer = "________________________________________________________________________________";
		System.out.println(buffer);
		debug_TA.setValue(debug_TA.getValue() + buffer+ "\n");
		debug_TA.setWidth("50em");
		add(debug_TA);
		
		
		//------------------------ Debug End --------------------------
	}

	private List<SMSRequest> createSMSList() {
		List<SMSRequest> input = new ArrayList<>();
    	SMSRequest smsRequest = new SMSRequest();
    	input.add(smsRequest);
    	smsRequest.setPhoneNumber("+61412068176");
    	smsRequest.setMessage("Tester");

    	SMSRequest smsRequest2 = new SMSRequest();
    	input.add(smsRequest2);
    	smsRequest2.setPhoneNumber("+61412068176");
    	smsRequest2.setMessage("Tester2");

    	SMSRequest smsRequest3 = new SMSRequest();
    	input.add(smsRequest3);
    	smsRequest3.setPhoneNumber("+61412068176");
    	smsRequest3.setMessage("Tester3");
		return input;
	}
	
	private String languageToCode (String language) {
		if ("Arabic".equals(language)) {
			return "ar";
		}
		if ("Chinese".equals(language)) {
			return "zh";
		}
		return "en";
	}
}
