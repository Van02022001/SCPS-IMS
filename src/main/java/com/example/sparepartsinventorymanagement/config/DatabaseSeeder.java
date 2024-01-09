package com.example.sparepartsinventorymanagement.config;

import com.example.sparepartsinventorymanagement.entities.NotificationTemplate;
import com.example.sparepartsinventorymanagement.entities.NotificationType;
import com.example.sparepartsinventorymanagement.entities.SourceType;
import com.example.sparepartsinventorymanagement.repository.NotificationTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {
    private final NotificationTemplateRepository notificationTemplateRepository;

//    @PostConstruct
//    public void init(){
//
//        for (NotificationType type : NotificationType.values()) {
//            // Call createNotificationTemplate for each type
//            createNotificationTemplate(generateTitle(type), type.name(), generateContent(type));
//        }
//    }



    private void createNotificationTemplate(String title, String type, String content) {
        try {
            NotificationTemplate notificationTemplate = NotificationTemplate.builder()
                    .title(title)
                    .type(NotificationType.valueOf(type))
                    .sourceType(SourceType.RECEIPT)
                    .content(content)
                    .createdAt(new Date()) // Ensure this matches the format expected by the database
                    .updatedAt(new Date()) // Ensure this is not null if your constraint requires it
                    .description("Thông báo") // Ensure this meets any constraints like length or content
                    .build();

            // Add a log to check the data before saving
            logDataBeforeSave(notificationTemplate);

            notificationTemplateRepository.save(notificationTemplate);

            System.out.println("Saved notification template: " + type);
        } catch (Exception e) {
            System.out.println("Failed to save notification template: " + type);
            e.printStackTrace();
        }
    }

    private void logDataBeforeSave(NotificationTemplate notificationTemplate) {
        // Logging the data to help identify what might be violating the constraint
        System.out.println("Attempting to save notification template with title: " + notificationTemplate.getTitle());
        System.out.println("Type: " + notificationTemplate.getType());
        System.out.println("Content: " + notificationTemplate.getContent());
        System.out.println("Source Type: " + notificationTemplate.getSourceType());
        System.out.println("Created At: " + notificationTemplate.getCreatedAt());
        System.out.println("Updated At: " + notificationTemplate.getUpdatedAt());
        System.out.println("Description: " + notificationTemplate.getDescription());
    }



    private String generateTitle(NotificationType type) {
        // Generate title based on the NotificationType
        // Modify this logic as per your requirement
        return type.name().replace("_", " ");
    }

    private String generateContent(NotificationType type) {
        // Generate content based on the NotificationType
        // Modify this logic as per your requirement
        return "Notification for " + type.name() + " with sourceId #{sourceId}";
    }
}
//        createNotificationTemplate("Yêu Cầu Nhập Kho", "YEU_CAU_NHAP_KHO" ,"Yêu cầu nhập kho #{sourceId} đã được tạo");
//        createNotificationTemplate("Xác Nhận Nhập Kho", "XAC_NHAN_NHAP_KHO", "Yêu cầu nhập kho #{sourceId} đã được xác nhận");
//        createNotificationTemplate("Tiến Hành Nhập Kho", "DANG_TIEN_HANH_NHAP_KHO", "Nhap kho #{sourceId} đang được tiến hành");
//        createNotificationTemplate("Nhập Kho Hoàn Tất", "NHAP_KHO_HOAN_TAT", "Nhập Kho #{sourceId} đã được hoàn tất");


//        createNotificationTemplate("Yêu Cầu Xuất Kho", "YEU_CAU_XUAT_KHO" ,"Yêu cầu xuất kho #{sourceId} đã được tạo");
//        createNotificationTemplate("Xác Nhận Xuất Kho", "XAC_NHAN_XUAT_KHO", "Yêu cầu xuất  kho #{sourceId} đã được xác nhận");
//        createNotificationTemplate("Tiến Hành Xuất Kho", "DANG_TIEN_HANH_XUAT_KHO", "Yêu cầu xuất  kho #{sourceId} đang được tiến hành");
//        createNotificationTemplate("Xuất Kho Hoàn Tất", "XUAT_KHO_HOAN_TAT", "Yêu cầu xuất  kho #{sourceId} đã được hoàn tất");
