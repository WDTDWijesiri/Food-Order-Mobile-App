package com.example.myapplication;

public class CartItem {
    private int id;
    private byte[] itemImage;
    private String itemName;
    private String itemDescription;
    private String itemSize;
    private double itemTotalPrice;

    public CartItem(int id, byte[] itemImage, String itemName, String itemDescription, String itemSize, double itemTotalPrice) {
        this.id = id;
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemSize = itemSize;
        this.itemTotalPrice = itemTotalPrice;
    }

    public int getId() {
        return id;
    }

    public byte[] getItemImage() {
        return itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemSize() {
        return itemSize;
    }

    public double getItemTotalPrice() {
        return itemTotalPrice;
    }


}
