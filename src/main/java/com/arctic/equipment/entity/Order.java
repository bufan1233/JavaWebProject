package com.arctic.equipment.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private Integer id;
    private Integer userId;
    private Integer itemId;
    private Integer quantity;
    private Date orderTime;
    private String status;

    // 联表查询时的扩展字段（用于在页面上显示买了什么菜，花了多少钱）
    private String itemName;
    private BigDecimal itemPrice;

    public Order() {}

    // 基础 Getter/Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // 扩展字段 Getter/Setter
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public BigDecimal getItemPrice() { return itemPrice; }
    public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }
}