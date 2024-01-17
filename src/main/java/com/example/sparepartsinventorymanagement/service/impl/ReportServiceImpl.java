package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ReportRequest;
import com.example.sparepartsinventorymanagement.dto.response.*;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserRepository userRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ItemRepository itemRepository;
    private final ReceiptRepository receiptRepository;
    @Override
    public ReportResponse getReport() {

        return ReportResponse.builder()
                .staffReportResponse(getStaffReportResponse())
                .subcategoryReportResponse(getSubcategoryReportResponse())
                .build();
    }

    @Override
    public ReceiptReportResponse getReceiptReport(ReportRequest request) {
        if(request.getType().equalsIgnoreCase("Ngày")){
            List<Receipt> totalReceipts = receiptRepository.findAll().stream()
                    .filter(receipt -> receipt.getLastModifiedDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate().isEqual(request.getDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate()))
                    .toList();
            return receiptReportResponse(totalReceipts);
        }else if(request.getType().equalsIgnoreCase("Tháng")){
            Date startDate = getFirstDayOfMonth(request.getDate());
            Date endDate = getLastDayOfMonth(request.getDate());
            List<Receipt> totalReceipts = receiptRepository.findAll().stream()
                    .filter(receipt -> !receipt.getLastModifiedDate().before(startDate)
                    && !receipt.getLastModifiedDate().after(endDate))
                    .toList();
            return receiptReportResponse(totalReceipts);
        }else if(request.getType().equalsIgnoreCase("Năm")){
            Date startDate = getFirstDayOfYear(request.getDate());
            Date endDate = getLastDayOfYear(request.getDate());
            List<Receipt> totalReceipts = receiptRepository.findAll().stream()
                    .filter(receipt -> !receipt.getLastModifiedDate().before(startDate)
                            && !receipt.getLastModifiedDate().after(endDate))
                    .toList();
            return receiptReportResponse(totalReceipts);
        }else{
            throw new InvalidResourceException("Loại không đúng.");
        }
    }

    private StaffReportResponse getStaffReportResponse(){
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getRole().getName().equals("ADMIN"))
                .toList();
        List<User> managers = users.stream()
                .filter(user -> user.getRole().getName().equals("MANAGER"))
                .toList();
        List<User> inventoryStaffs = users.stream()
                .filter(user -> user.getRole().getName().equals("INVENTORY_STAFF"))
                .toList();
        List<User> saleStaffs = users.stream()
                .filter(user -> user.getRole().getName().equals("SALE_STAFF"))
                .toList();
        return StaffReportResponse.builder()
                .totalStaff(users.size())
                .totalInventoryStaff(inventoryStaffs.size())
                .totalManager(managers.size())
                .totalSaleStaff(saleStaffs.size())
                .build();
    }
    private SubcategoryReportResponse getSubcategoryReportResponse(){
        List<SubCategory> categories = subCategoryRepository.findAll();
        List<Item> items = itemRepository.findAll();

        List<Receipt> totalReceipts = receiptRepository.findAll();
        List<Receipt> totalImportReceipts = totalReceipts.stream()
                .filter(receipt -> receipt.getType().equals(ReceiptType.PHIEU_NHAP_KHO)
                        && receipt.getStatus().equals(ReceiptStatus.Completed))
                .toList();
        List<Receipt> totalExportReceipts = totalReceipts.stream()
                .filter(receipt ->  receipt.getType().equals(ReceiptType.PHIEU_XUAT_KHO)
                        && receipt.getStatus().equals(ReceiptStatus.Completed))
                .toList();
        double totalImportValue = 0;
        int totalItemImportQuantity = 0;
        for (Receipt receipt: totalImportReceipts
        ) {
            totalImportValue += receipt.getTotalPrice();
            totalItemImportQuantity += receipt.getTotalQuantity();
        }
        double totalExportValue = 0;
        int totalItemExportQuantity = 0;
        for (Receipt receipt: totalExportReceipts
        ) {
            totalExportValue += receipt.getTotalPrice();
            totalItemExportQuantity += receipt.getTotalQuantity();
        }
        double totalInventoryValue = 0;
        int totalItemQuantity = 0;
        int totalDefectiveItemQuantity = 0;
        int totalLostItemQuantity = 0;
        for (Item item: items
             ) {
            if(!item.getInventoryList().isEmpty()){
                for (Inventory inventory: item.getInventoryList()
                ) {
                    totalInventoryValue += inventory.getTotalValue();
                }
            }
            totalItemQuantity += item.getQuantity();
            totalDefectiveItemQuantity += item.getDefective();
            totalLostItemQuantity += item.getLost();
        }

        return SubcategoryReportResponse.builder()
                .totalSubcategory(categories.size())
                .totalItem(items.size())
                .totalInventoryValue(totalInventoryValue)
                .totalItemQuantity(totalItemQuantity)
                .totalItemExportQuantity(totalItemExportQuantity)
                .totalItemImportQuantity(totalItemImportQuantity)
                .totalItemImportValue(totalImportValue)
                .totalItemExportValue(totalExportValue)
                .totalDefectiveItemQuantity(totalDefectiveItemQuantity)
                .totalLostItemQuantity(totalLostItemQuantity)
                .build();
    }
    private Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        resetTime(calendar);
        return calendar.getTime();
    }

    private Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setEndTime(calendar);
        return calendar.getTime();
    }
    // Phương thức để lấy ngày đầu tiên của năm
    private Date getFirstDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        resetTime(calendar);
        return calendar.getTime();
    }

    // Phương thức để lấy ngày cuối cùng của năm
    private Date getLastDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        setEndTime(calendar);
        return calendar.getTime();
    }

    // Cài đặt giờ, phút, giây, và mili giây về 0
    private void resetTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    // Cài đặt giờ, phút, giây, và mili giây về cuối ngày
    private void setEndTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
    private List<Receipt> getImportReceipts(List<Receipt> totalReceipts){
        return totalReceipts.stream()
                .filter(receipt -> receipt.getType().equals(ReceiptType.PHIEU_NHAP_KHO)
                        && receipt.getStatus().equals(ReceiptStatus.Completed))
                .toList();
    }
    private List<Receipt> getExportReceipts(List<Receipt> totalReceipts){
        return totalReceipts.stream()
                .filter(receipt -> receipt.getType().equals(ReceiptType.PHIEU_NHAP_KHO)
                        && receipt.getStatus().equals(ReceiptStatus.Completed))
                .toList();
    }

    private ReceiptReportResponse receiptReportResponse(List<Receipt> totalReceipts){
        List<Receipt> importReceipts = getImportReceipts(totalReceipts);
        double importedValue = 0;
        int numberImportItem = 0;
        for (Receipt receipt: importReceipts
        ) {
            importedValue += receipt.getTotalPrice();
            numberImportItem += receipt.getTotalQuantity();
        }
        List<Receipt> totalExportReceipts = getExportReceipts(totalReceipts);
        double exportedValue = 0;
        int numberExportItem = 0;
        for (Receipt receipt: totalExportReceipts
        ) {
            exportedValue += receipt.getTotalPrice();
            numberExportItem += receipt.getTotalQuantity();
        }
        return ReceiptReportResponse.builder()
                .importedItemValue(importedValue)
                .exportedItemValue(exportedValue)
                .numberImportItem(numberImportItem)
                .numberExportItem(numberExportItem)
                .build();
    }
}
