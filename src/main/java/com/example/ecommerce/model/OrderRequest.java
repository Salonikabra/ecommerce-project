package com.example.ecommerce.model;

import lombok.Data;
import lombok.ToString;


@ToString
@Data
public class OrderRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String paymenttype;
}
