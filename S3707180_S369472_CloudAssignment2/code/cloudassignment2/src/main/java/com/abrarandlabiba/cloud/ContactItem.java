package com.abrarandlabiba.cloud;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Contact")
public class ContactItem {

    public final static String CONTACT_TABLE_NAME = "Contact";
    private String email;
    private String SMS;
    private String language;

    @DynamoDBHashKey(attributeName="Email")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName="SMS")
    public String getSMS() {
        return this.SMS;
    }

    public void setSMS(String SMS) {
        this.SMS = SMS;
    }
    
    
    @DynamoDBAttribute(attributeName="language")
    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}