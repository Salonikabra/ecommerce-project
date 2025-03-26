package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(length=500)
    private String title;
    @Column(length=40000)
    private String description;
    private String category;
    private double price;
    private int stock;
    private String image;
    private int discount;
    private Double discountprice;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isactive;
}
