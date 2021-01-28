package com.abrarandlabiba.cloud.view;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.abrarandlabiba.cloud.ContactItem;
import com.abrarandlabiba.cloud.service.TableService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;


@Route("admin")
@CssImport("./styles/shared-styles.css" )
public class AdminView extends VerticalLayout {

	private static final long serialVersionUID = -3702687262495231414L;

    private Grid<ContactItem> contacts_Grid;
    
    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
	public AdminView(@Autowired TableService tableService) {
		
		// Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content"); 

        
        HorizontalLayout headerButton_HL = new HorizontalLayout();
        add(headerButton_HL);
		Button toMain_BT = new Button("< Back to Main Page", e -> UI.getCurrent().getPage().setLocation("/"));
		headerButton_HL.add(toMain_BT);

		Button toDebug_BT = new Button("to Debug Page > ", e -> UI.getCurrent().getPage().setLocation("/debug"));
		headerButton_HL.add(toDebug_BT);
		
        HorizontalLayout contact_HL = new HorizontalLayout();
        add(contact_HL);
        
        TextField email_TF = new TextField();
        contact_HL.add(email_TF);
        email_TF.setPlaceholder("Email: e.g. john.do@random.com");
        
        /*--------------------------------*/ email_TF.setValue("");

        TextField SMS_TF = new TextField();
        contact_HL.add(SMS_TF);
        SMS_TF.setPlaceholder("Phone: e.g. +614XXXXXXXX");
        SMS_TF.setValue("");
        
         
		RadioButtonGroup<String> language_RG = new RadioButtonGroup<>();
		contact_HL.add(language_RG);
//		language_RG.setLabel("Language");
		language_RG.setItems("English","Chinese", "Arabic");
//		language_RG.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		language_RG.setValue("English");
		language_RG.setRequired(true);
        
		Button addContact_BT = new Button("Add Contact", e -> {
			Notification.show(tableService.addContact(email_TF.getValue(), SMS_TF.getValue(), language_RG.getValue()));
			UI.getCurrent().getPage().setLocation("/admin");
		});
		contact_HL.add(addContact_BT);
		Icon addContact_Icon = new Icon(VaadinIcon.PLUS_CIRCLE);
		addContact_BT.setIcon(addContact_Icon);
		
		List<ContactItem> contacts = new ArrayList<>();
		Notification.show(tableService.loadContacts(contacts));
		contacts_Grid = newContactsGrid(contacts);
		add(contacts_Grid);
		
		
        add(new Label());
        add(new Label());
        
        add(new Label("Administrative Functions:")); 
        
        HorizontalLayout adminButtons_HL = new HorizontalLayout();
        add(adminButtons_HL);
        
        Button createTable_BT = new Button("Create Table", e -> 
        Notification.show(tableService.createTable()));
		createTable_BT.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		createTable_BT.setIcon(new Icon(VaadinIcon.CHECK_CIRCLE_O));
		adminButtons_HL.add(createTable_BT); 
  
		Button deleteTable_BT = new Button("Delete Table", e -> Notification.show(tableService.deleteTable()));
		deleteTable_BT.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Icon deleteIcon = new Icon(VaadinIcon.BAN);
		deleteIcon.setColor("red");
		deleteTable_BT.setIcon(deleteIcon);
        
		adminButtons_HL.add(deleteTable_BT);

	}

	
	private Grid<ContactItem> newContactsGrid(List<ContactItem> contacts) {
		Grid<ContactItem> grid = new Grid<>();
		grid.setItems(contacts);
		grid.addColumn(ContactItem::getEmail).setHeader("Email");
		grid.addColumn(ContactItem::getSMS).setHeader("Phone");
		grid.addColumn(ContactItem::getLanguage).setHeader("Language");
		grid.setWidth("40em");
		return grid;
	}
}
