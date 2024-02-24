package com.example.c195_version1;

/**
 * Customer's appointment class
 */
public class CustomerAppointment {
    private  String customerName;
    private  int numberOfAppointments;

    /**
     * Constructor for Customer's appointments
     * @param customerName
     * @param numberOfAppointments
     */
    public CustomerAppointment(String customerName, int numberOfAppointments) {
        this.customerName = customerName;
        this.numberOfAppointments = numberOfAppointments;
    }

    /**
     * getter for customer name
     * @return Customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * getter for number of appointments
     * @return number of appointments
     */
    public int getNumberOfAppointments() {
        return numberOfAppointments;
    }
}
