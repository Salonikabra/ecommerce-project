package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderid;
    private LocalDate orderdate;

    @ManyToOne
    private Product product;
    private Double price;
    private Integer quantity;
    @ManyToOne
    private User user;
    private String status;
    private String paymenttype;
    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;
}
