package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CheckInventoryReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.ImportRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.InventoryCheckDetail;
import com.example.sparepartsinventorymanagement.dto.request.UpdateImportRequestReceipt;
import com.example.sparepartsinventorymanagement.dto.response.CheckInventoryReceiptResponse;
import com.example.sparepartsinventorymanagement.dto.response.ExportReceiptResponse;
import com.example.sparepartsinventorymanagement.dto.response.ImportRequestReceiptResponse;
import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.Notification;

import java.util.List;
import java.util.Map;

public interface ReceiptService {


    ImportRequestReceiptResponse createImportRequestReceipt(ImportRequestReceiptForm importRequestReceiptForm);

    List<ImportRequestReceiptResponse> getAllImportRequestReceipts();

    List<ImportRequestReceiptResponse> getAllImportRequestReceiptsByWareHouse();
    List<ImportRequestReceiptResponse> getAllImportReceiptsByWareHouse();
    List<ImportRequestReceiptResponse> getAllImportReceipts();


    ImportRequestReceiptResponse getImportRequestReceiptById(Long id);
    ImportRequestReceiptResponse getImportReceiptById(Long id);


    ImportRequestReceiptResponse updateImportRequestReceipt(Long id, UpdateImportRequestReceipt importRequestReceiptForm);


    void deleteImportRequestReceipt(Long id);

    void deleteImportReceipt(Long id);

    void confirmImportRequestReceipt(Long receiptId);

    void  startImportProcess(Long receiptId);

    ImportRequestReceiptResponse createImportReceipt(Long receiptId, Map<Long, Integer> actualQuantity);
    ImportRequestReceiptResponse updateImportReceipt(Long receiptId, Map<Long, Integer> actualQuantities);
    ExportReceiptResponse createExportReceipt(Long receiptId, Map<Long, Integer> actualQuantities);
   // ExportReceiptResponse updateExportReceipt(Long receiptId, Map<Long, Integer> actualQuantities);

    List<ExportReceiptResponse> getAllExportReceipts();

    List<ExportReceiptResponse> getAllExportReceiptsByWareHouse();


    CheckInventoryReceiptResponse createCheckInventoryReceipt(CheckInventoryReceiptForm checkInventoryReceiptForm);
    List<CheckInventoryReceiptResponse>  getAllCheckInventoryReceipts();
    CheckInventoryReceiptResponse  getCheckInventoryReceiptById(Long receiptId);

    void confirmCheckingInventoryReceipt(Long receiptId);
    ExportReceiptResponse getExportReceiptById(Long id);

    void deleteExportReceipt(Long id);
}
