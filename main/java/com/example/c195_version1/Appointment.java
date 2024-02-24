package com.example.c195_version1;

import java.sql.ResultSet;

/**
 * Appointment class
 */
public class Appointment {

    private int apptId;
    private String title;
    private String description;
    private String location;
    private int contactId;
    private String month;
    private int total;
    private String type;
    private String startDate;
    private String endDate;
    private String endTime;
    private int customerId;
    private int userId;

    /**
     * Constructor for Appointments
     * @param apptId appointment ID
     * @param title appointment title
     * @param description appointment description
     * @param location appointment location
     * @param contactId appointment contact
     * @param type appointment type
     * @param startTime appointment start date/time
     * @param endTime appointment end date/time
     * @param customerId appointment customer
     * @param userId appointment user
     */
    public Appointment(int apptId, String title, String description, String location, int contactId, String type, String startTime, String endTime, int customerId, int userId) {
        this.apptId = apptId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactId = contactId;
        this.type = type;
        this.startDate = startTime;
        this.endTime = endTime;
        this.customerId = customerId;
        this.userId = userId;
    }

    /**
     * Appointment constructor that loads the report table by month and type
     * @param month appointment month
     * @param type appointment type
     * @param total appointment total by month and type
     */
    public Appointment(String month,String type, int total) {
        this.month = month;
        this.total = total;
        this.type = type;
    }

    public Appointment(ResultSet resultSet) {
    }


    /**
     * Gets appointment ID
     * @return apptId
     */
    public int getApptId() {
        return apptId;
    }

    /**
     * setter for apptId
     * @param apptId sets apptId
     */
    public void setApptId(int apptId) {
        this.apptId = apptId;
    }

    /**
     * gets title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * setter for title
     * @param title sets title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * getter for description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * setter for description
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * getter for location
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * setter for location
     * @param location location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * getter for contact ID
     * @return contact ID
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * setter for contact ID
     * @param contactId contact ID
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * getter for type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * setter for type
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * getter for start date/time
     * @return start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * setter for start date/time
     * @param startDate start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * getter for end date/time
     * @return end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * setter for end date/time
     * @param endTime end time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * getter for customer ID
     * @return customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * setter for customer ID
     * @param customerId customer ID
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * getter for user ID
     * @return userID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * getter for month
     * @return month
     */
    public String getMonth() {
        return month;
    }

    /**
     * setter for month
     * @param month month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * getter for total
     * @return total
     */
    public int getTotal() {
        return total;
    }

    /**
     * setter for total
     * @param total total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * getter for end date
     * @return end date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * setter for end date
     * @param endDate end date
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * setter for user id
     * @param userId user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}

