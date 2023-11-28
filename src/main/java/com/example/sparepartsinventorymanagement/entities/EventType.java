package com.example.sparepartsinventorymanagement.entities;

public enum EventType {
    STOCK_ALERT,
    CREATED,
    UPDATED,
    DELETED,
    APPROVED,
    REJECTED, // Từ chối
    REQUESTED, // Yêu cầu
    CONFIRMED
}
