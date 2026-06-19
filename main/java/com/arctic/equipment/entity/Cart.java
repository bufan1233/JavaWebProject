package com.arctic.equipment.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    // 组合一个内部类，用来封装“菜品”与“购买数量”
    public static class CartItem {
        private MenuItem menuItem;
        private int count;

        public CartItem(MenuItem menuItem, int count) {
            this.menuItem = menuItem;
            this.count = count;
        }

        public MenuItem getMenuItem() { return menuItem; }
        public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        // 计算当前单项商品的总价
        public BigDecimal getItemTotalPrice() {
            return menuItem.getPrice().multiply(new BigDecimal(count));
        }
    }

    // 购物车条目列表
    private List<CartItem> items = new ArrayList<>();

    /**
     * 添加菜品到购物车
     */
    public void add(MenuItem menuItem, int count) {
        // 1. 检查购物车里是否已经有该菜品
        for (CartItem item : items) {
            if (item.getMenuItem().getId().equals(menuItem.getId())) {
                // 有则累加数量
                item.setCount(item.getCount() + count);
                return;
            }
        }
        // 2. 没有则新建条目
        items.add(new CartItem(menuItem, count));
    }

    /**
     * 更新购物车中某个菜品的数量
     */
    public void updateCount(Integer itemId, int count) {
        if (count <= 0) {
            remove(itemId);
            return;
        }
        for (CartItem item : items) {
            if (item.getMenuItem().getId().equals(itemId)) {
                item.setCount(count);
                break;
            }
        }
    }

    /**
     * 从购物车中移除某个菜品
     */
    public void remove(Integer itemId) {
        items.removeIf(item -> item.getMenuItem().getId().equals(itemId));
    }

    /**
     * 清空购物车
     */
    public void clear() {
        items.clear();
    }

    /**
     * 获取购物车中所有条目
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * 计算购物车内所有商品的总金额
     */
    public BigDecimal getTotalMoney() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getItemTotalPrice());
        }
        return total;
    }

    /**
     * 获取购物车内商品总件数
     */
    public int getTotalCount() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getCount();
        }
        return total;
    }
}