package com.example.c195_version1;

import javafx.collections.ObservableList;

/**
 * Class for customer
 */
public class Customer {

    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNo;
    private ObservableList custCountry;
    private String custState;
    private String state;

    /**
     * Constructor for customer
     * @param customerId
     * @param customerName
     * @param address
     * @param postalCode
     * @param phoneNo
     */
    public Customer(int customerId, String customerName, String address, String  postalCode, String phoneNo) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNo = phoneNo;

    }

    /**
     * getter for customer ID
     * @return customer id
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * setter for customer id
     * @param customerId
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * getter for customer name
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * setter for customer
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * getter for address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * setter for address
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * setter for postal code
     * @return postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * setter for postal code
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * getter for phone number
     * @return phone number
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * setter for phone number
     * @param phoneNo
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    /**
     * getter for Observable list for customer's country
     * @return
     */
    public ObservableList getCustCountry() {
        return custCountry;
    }

    /**
     * setter for customer country
     * @param custCountry
     */
    public void setCustCountry(ObservableList custCountry) {
        this.custCountry = custCountry;
    }

    /**
     * getter for state
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * setter for state
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }
}



