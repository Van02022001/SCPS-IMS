package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.*;
import com.example.sparepartsinventorymanagement.dto.response.*;
import com.example.sparepartsinventorymanagement.entities.Notification;
import com.example.sparepartsinventorymanagement.entities.ReceiptDetail;

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
    void cancelImportRequestReceipt(Long receiptId);

    ExportReceiptResponse getExportReceiptById(Long id);

    void deleteExportReceipt(Long id);
    List<ReceiptDetailDTO> getItemsNullLocation(Long id);
}
