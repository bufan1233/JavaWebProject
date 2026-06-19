package com.arctic.equipment.entity;

import java.math.BigDecimal; // 必须导入

public class User {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private BigDecimal balance; // 新增：用户余额

    public User() {}
    public User(Integer id, String username) { this.id = id; this.username = username; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // 新增 Getter/Setter
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}