package com.example.sparepartsinventorymanagement.entities;

public enum CustomerRequestReceiptStatus {
    Draft,//nháp
    Pending_Approval,  // chờ duyệt
    Approved, //đã duyệt
    Completed, // hoàn thành
    Rejected, // từ chối

    Canceled, // Hủy bỏ
    IN_PROGRESS
}
