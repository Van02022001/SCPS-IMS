package com.example.sparepartsinventorymanagement.config;

import com.example.sparepartsinventorymanagement.entities.NotificationTemplate;
import com.example.sparepartsinventorymanagement.entities.NotificationType;
import com.example.sparepartsinventorymanagement.entities.SourceType;
import com.example.sparepartsinventorymanagement.repository.NotificationTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {
    private final NotificationTemplateRepository notificationTemplateRepository;

//    @PostConstruct
//    public void init(){
//        createNotificationTemplate("Yêu Cầu Nhập Kho", "YEU_CAU_NHAP_KHO" ,"Yêu cầu nhập kho #{sourceId} đã được tạo");
//        createNotificationTemplate("Xác Nhận Nhập Kho", "XAC_NHAN_NHAP_KHO", "Yêu cầu nhập kho #{sourceId} đã được xác nhận");
//        createNotificationTemplate("Yêu Cầu Xuất Kho", "YEU_CAU_XUAT_KHO" ,"Yêu cầu xuất kho #{sourceId} đã được tạo");
//        createNotificationTemplate("Xác Nhận Xuất Kho", "XAC_NHAN_XUAT_KHO", "Yêu cầu xuất  kho #{sourceId} đã được xác nhận");
//    }

    private void createNotificationTemplate(String title, String type, String content){
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
                .title(title)
                .type(NotificationType.valueOf(type))
                .sourceType(SourceType.RECEIPT)
                .content(content)
                .build();
        notificationTemplateRepository.save(notificationTemplate);
    }
}
