package com.abrarandlabiba.cloud.service;

import static com.abrarandlabiba.cloud.ContactItem.CONTACT_TABLE_NAME;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.abrarandlabiba.cloud.ContactItem;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;;

@Service
public class TableService implements Serializable{

	private static final long serialVersionUID = -3897122927755419609L;

	static AmazonDynamoDB dbClient = AmazonDynamoDBClientBuilder.standard()
	  .withRegion(Regions.US_EAST_1)
//    .withCredentials(new ProfileCredentialsProvider("default"))
	  .build();
	
	static DynamoDB dynamoDB = new DynamoDB(dbClient);
  
    static SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public synchronized String deleteTable() {
		String retMsg = "";
    	
    	try {
    		Table table = dynamoDB.getTable(CONTACT_TABLE_NAME);
        
            System.out.println("Issuing DeleteTable request for " + CONTACT_TABLE_NAME);
            table.delete();
            System.out.println("Waiting for " + CONTACT_TABLE_NAME
                + " to be deleted...this may take a while...");
            table.waitForDelete();
            
            retMsg = "Delete Table Success for " + CONTACT_TABLE_NAME;
           
        } catch (Exception e) {
            retMsg += "DeleteTable request failed for '" + CONTACT_TABLE_NAME + "' " + e.getMessage();
        }
    	
        System.out.println(retMsg);
    	return retMsg;
    }

	public synchronized String createTable() {
		String retMsg = "";

		
		try {
			createTable(CONTACT_TABLE_NAME, 5L, 5L, "Email", "S", null, null);
            retMsg = "Create Table Success " + CONTACT_TABLE_NAME;
            
		} catch (Exception e) {
			retMsg += "CreateTable request failed for '" + CONTACT_TABLE_NAME + "' " + e.getMessage(); 
		}
		
        System.out.println(retMsg);
        return retMsg;
	}

    private void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits, 
        String partitionKeyName, String partitionKeyType, 
        String sortKeyName, String sortKeyType) throws Exception{
    	
            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                .withAttributeName(partitionKeyName)
                .withKeyType(KeyType.HASH)); //Partition key
            
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName(partitionKeyName)
                .withAttributeType(partitionKeyType));

//            if (sortKeyName != null) {
//                keySchema.add(new KeySchemaElement()
//                    .withAttributeName(sortKeyName)
//                    .withKeyType(KeyType.RANGE)); //Sort key
//                attributeDefinitions.add(new AttributeDefinition()
//                    .withAttributeName(sortKeyName)
//                    .withAttributeType(sortKeyType));
//            }

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput( new ProvisionedThroughput()
                        .withReadCapacityUnits(readCapacityUnits)
                        .withWriteCapacityUnits(writeCapacityUnits));

            request.setAttributeDefinitions(attributeDefinitions);

            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.out.println("Waiting for " + tableName
                + " to be created...this may take a while...");
            table.waitForActive();
    }

  
    public synchronized String addContact_old(String email, String SMS) {
		String retMsg = "";
        try {
        	
        	//Validate input
        	
            Table table = dynamoDB.getTable(CONTACT_TABLE_NAME);
            System.out.println("adding contact to table " + CONTACT_TABLE_NAME);

			Item item = new Item().withPrimaryKey("Email", email).withString("SMS", SMS);

			table.putItem(item);

			retMsg = "Updated Table " + CONTACT_TABLE_NAME + "with: '" + email + ":" + SMS;
        } catch (Exception e) {
        	
			retMsg = "Failed to add items in '" + CONTACT_TABLE_NAME + "' " + e.getMessage(); 
        }

        System.out.println(retMsg);
    	return retMsg;
    }

    public synchronized String addContact(String email, String SMS, String language) {
		String retMsg = "";
        try {
        	
        	//Validate input
        	if (email == null || !email.contains("@")) {
        		retMsg = "Email address is not vaild. e.g. abc@xyz.com";
        		return retMsg;
        	}

        	if (SMS == null || !SMS.startsWith("+614") || SMS.length() != 12) {
        		retMsg = "SMS address is not vaild. e.g. +614XXXXXXXX";
        		return retMsg;
        	}
        	
        	if (language == null || ! (language.equals("English") || language.equals("Chinese") || language.equals("Arabic"))) {
        		language = "English";
        	}
        	
            System.out.println("adding contact to table " + CONTACT_TABLE_NAME);

            DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
            ContactItem contact = new ContactItem();
            System.out.println("adding contact to table 11" + CONTACT_TABLE_NAME);
            contact.setEmail(email);
            contact.setSMS(SMS);
            contact.setLanguage(language);
            System.out.println("adding contact to table 22" + CONTACT_TABLE_NAME);
            mapper.save(contact);
            
            System.out.println("adding contact to table 33" + CONTACT_TABLE_NAME);
            
			retMsg = "contact added to contact table" + CONTACT_TABLE_NAME + "with: '" + email + ":" + SMS;
        } catch (Exception e) {
        	
			retMsg = "Failed to add item in '" + CONTACT_TABLE_NAME + "' " + e.getMessage(); 
        }

        System.out.println(retMsg);
    	return retMsg;
    }

    
    public synchronized String loadContacts(List<ContactItem> contacts) {
		String retMsg = "";
        try {
        	
            System.out.println("loading contacts from table " + CONTACT_TABLE_NAME);


            DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
            PaginatedScanList<ContactItem> scanList = mapper.scan(ContactItem.class, new DynamoDBScanExpression());
            
            contacts.clear();
            contacts.addAll(scanList.subList(0, scanList.size()));

            for (ContactItem ci : scanList) {
            	System.out.println(ci.getEmail() + " " + ci.getSMS());
            }
            
            retMsg = "Load success from " + CONTACT_TABLE_NAME + ", size:" + scanList.size();
        } catch (Exception e) {
        	
			retMsg = "Failed to load items from '" + CONTACT_TABLE_NAME + "' " + e.getMessage(); 
        }

        System.out.println(retMsg);
    	return retMsg;
    }
    
 
}