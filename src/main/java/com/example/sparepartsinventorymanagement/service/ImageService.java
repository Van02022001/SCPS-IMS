package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.response.ImageDTO;
import com.example.sparepartsinventorymanagement.dto.response.UserDTO;
import com.example.sparepartsinventorymanagement.entities.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<ImageDTO> getImagesBySubCategory(Long scId);
    ImageDTO getImageById(Long id);
    ImageDTO uploadItemImage(Long scId, MultipartFile multipartFile);
    UserDTO uploadUserImage(MultipartFile multipartFile);
    boolean deleteImage(Long id);
}
