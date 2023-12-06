package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.ImportRequestReceiptForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateImportRequestReceipt;
import com.example.sparepartsinventorymanagement.dto.response.ExportReceiptResponse;
import com.example.sparepartsinventorymanagement.dto.response.ImportRequestReceiptResponse;
import com.example.sparepartsinventorymanagement.dto.response.NotificationDTO;
import com.example.sparepartsinventorymanagement.entities.Notification;

import java.util.List;
import java.util.Map;

public interface ReceiptService {


    ImportRequestReceiptResponse createImportRequestReceipt(ImportRequestReceiptForm importRequestReceiptForm);

    List<ImportRequestReceiptResponse> getAllImportRequestReceipts();
    List<ImportRequestReceiptResponse> getAllImportReceipts();


    ImportRequestReceiptResponse getImportRequestReceiptById(Long id);
    ImportRequestReceiptResponse getImportReceiptById(Long id);


    ImportRequestReceiptResponse updateImportRequestReceipt(Long id, UpdateImportRequestReceipt importRequestReceiptForm);


    void deleteImportRequestReceipt(Long id);

    void deleteImportReceipt(Long id);

    void confirmImportRequestReceipt(Long receiptId);

    void  startImportProcess(Long receiptId);

    ImportRequestReceiptResponse createImportReceipt(Long receiptId, Map<Long, Integer> actualQuantity);
    ExportReceiptResponse createExportReceipt(Long receiptId, Map<Long, Integer> actualQuantities);
}
