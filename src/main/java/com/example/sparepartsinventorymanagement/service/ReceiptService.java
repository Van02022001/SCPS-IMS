package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.*;
import com.example.sparepartsinventorymanagement.dto.response.*;

import java.util.List;
import java.util.Map;

public interface ReceiptService {


    ImportRequestReceiptResponse createImportRequestReceipt(ImportRequestReceiptForm importRequestReceiptForm);

    ImportRequestReceiptResponse createInternalRequestReceipt(ImportRequestReceiptForm internalRequestReceiptForm);
    void confirmInternalImportRequestReceipt(Long receiptId);

    void  startInternalImportProcess(Long receiptId);

    ImportRequestReceiptResponse createInternalImportReceipt(Long receiptId, Map<Long, Integer> actualQuantities);

    List<ImportRequestReceiptResponse> getAllInternalImportRequestReceipts();

    ImportRequestReceiptResponse getInternalImportRequestReceiptById(Long id);


    List<ImportRequestReceiptResponse> getAllInternalImportRequestReceiptsByWareHouse();

    List<ImportRequestReceiptResponse> getAllInternalImportReceipts();

    ImportRequestReceiptResponse getInternalImportReceiptById(Long id);

    List<ImportRequestReceiptResponse> getAllInternalImportReceiptsByWareHouse();


    //for internal-export
    ImportRequestReceiptResponse createInternalExportRequestReceipt(ImportRequestReceiptForm internalRequestReceiptForm);

    void confirmInternalExportRequestReceipt(Long receiptId);

    void  startInternalExportProcess(Long receiptId);

    List<ImportRequestReceiptResponse> getAllInternalExportRequestReceipts();

    ImportRequestReceiptResponse getInternalExportRequestReceiptById(Long id);

    List<ImportRequestReceiptResponse> getAllInternalExportRequestReceiptsByWareHouse();

    List<ImportRequestReceiptResponse> getAllInternalExportReceipts();

    ImportRequestReceiptResponse getInternalExportReceiptById(Long id);
    List<ImportRequestReceiptResponse> getAllInternalExportReceiptsByWareHouse();

    ImportRequestReceiptResponse createInternalExportReceipt(Long receiptId, Map<Long, Integer> actualQuantities);



    //for  import receipt
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
