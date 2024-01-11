package com.example.sparepartsinventorymanagement.entities;

public enum WarehouseTransferStatus {
    Draft,//nháp
    Pending_Approval,  // chờ duyệt
    Approved, //đã duyệt
    Completed, // hoàn thành
    Rejected, // từ chối

    Canceled, // Hủy bỏ

    IN_PROGRESS,

    NOT_COMPLETED

}
