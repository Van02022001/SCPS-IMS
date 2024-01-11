package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.BrandFromRequest;
import com.example.sparepartsinventorymanagement.dto.response.GetBrandDTO;
import com.example.sparepartsinventorymanagement.dto.response.ImageDTO;
import com.example.sparepartsinventorymanagement.dto.response.UserDTO;
import com.example.sparepartsinventorymanagement.service.impl.ImageServiceImpl;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "image")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private final ImageServiceImpl imageService;


    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF') or hasRole('ROLE_SALE_STAFF')")
    @Operation(summary = "For get list of image by subcategory")
    @GetMapping(value = "/subcategory/{scId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getImagesBySubCategory(
            @Parameter(description = "Enter brand to get", example = "1", required = true)
            @PathVariable(name = "scId") @NotBlank @NotEmpty Long scId
    ) {
        List<ImageDTO> res = imageService.getImagesBySubCategory(scId);
        if(res.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(),
                    "Danh sách trống",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Lấy danh sách ảnh của danh mục thành công.",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF') or hasRole('ROLE_SALE_STAFF')")
    @Operation(summary = "For get image by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBrandById(
            @Parameter(description = "Enter id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        ImageDTO res = imageService.getImageById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(),
                "Lấy ảnh thành công.",
                res
        ));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "For upload item image")
    @PostMapping(value = "/subcategory/{scId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadItemImage(
            @Parameter(description = "Enter subcategory id to get", example = "1", required = true)
            @PathVariable(name = "scId") @NotBlank @NotEmpty Long scId,
            @RequestPart("file") MultipartFile file

    ) {
        ImageDTO res = imageService.uploadItemImage(scId, file);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Tải lên ảnh thất bại.",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.toString(),
                "Tải lên ảnh thành công.",
                res
        ));
    }
    @Operation(summary = "For upload user image")
    @PostMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadUserImage(
            @RequestPart("file") MultipartFile file
    ) {
        UserDTO res = imageService.uploadUserImage(file);
        if(res == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Tải lên ảnh thất bại.",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.toString(),
                "Tải lên ảnh thành công.",
                res
        ));
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_INVENTORY_STAFF') or hasRole('ROLE_SALE_STAFF')")
    @Operation(summary = "For delete image by id")
    @DeleteMapping (value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteImage(
            @Parameter(description = "Enter id to get", example = "1", required = true)
            @PathVariable(name = "id") @NotBlank @NotEmpty Long id
    ) {
        boolean res = imageService.deleteImage(id);
        if(res){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Xóa ảnh thành công.",
                    null
            ));
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(),
                    "Xóa ảnh thất bại.",
                    null
            ));
        }
    }
}
