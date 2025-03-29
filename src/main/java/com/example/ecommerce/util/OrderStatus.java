package com.example.ecommerce.util;

public enum OrderStatus {
    IN_PROGRESS(1,"In Progess"),
    ORDER_RECEIVED(2,"Order_Received"),
    PRODUCT_PACKED(3,"product Packed"),
    OUT_FOR_DELIVERY(4,"Out for Delivery"),

    DELIVERED(5,"Delivered"),
    CANCEL(6,"Cancelled"),
    SUCCESS(7,"Success");

    OrderStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String name;

}
