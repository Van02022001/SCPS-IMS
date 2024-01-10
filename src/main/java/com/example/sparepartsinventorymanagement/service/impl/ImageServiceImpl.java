package com.example.sparepartsinventorymanagement.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobUploadFromFileOptions;
import com.azure.storage.blob.options.BlobUploadFromUrlOptions;
import com.azure.storage.blob.specialized.BlobLeaseClient;
import com.example.sparepartsinventorymanagement.dto.response.ImageDTO;
import com.example.sparepartsinventorymanagement.dto.response.UserDTO;
import com.example.sparepartsinventorymanagement.entities.Image;
import com.example.sparepartsinventorymanagement.entities.SubCategory;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.exception.InvalidResourceException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
import com.example.sparepartsinventorymanagement.repository.ImageRepository;
import com.example.sparepartsinventorymanagement.repository.SubCategoryRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final BlobContainerClient blobContainerClient;
    private final ImageRepository imageRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    @Override
    public List<ImageDTO> getImagesBySubCategory(Long scId) {
        SubCategory subCategory = subCategoryRepository.findById(scId).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy danh mục")
        );
        List<Image> images = imageRepository.findBySubCategory(subCategory);
        return mapper.map(images, new TypeToken<List<ImageDTO>>(){}.getType());
    }

    @Override
    public ImageDTO getImageById(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy ảnh")
        );
        return mapper.map(image, ImageDTO.class);
    }

    @Override
    public UserDTO uploadUserImage(MultipartFile multipartFile) {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrinciple.getId()).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy người dùng")
        );
        BlobClient blob = blobContainerClient
                .getBlobClient(multipartFile.getOriginalFilename());
        checkImage(multipartFile);
        try {
            blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);
        } catch (IOException e) {
            throw new InvalidResourceException("Lỗi khi tải lên ảnh: " + e);
        }
        user.setImage(blob.getBlobUrl());
        return mapper.map(user, UserDTO.class);
    }

    @Override
    public ImageDTO uploadItemImage(Long scId, MultipartFile multipartFile) {
        SubCategory subCategory = subCategoryRepository.findById(scId).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy danh mục")
        );

        BlobClient blob = blobContainerClient
                .getBlobClient(multipartFile.getOriginalFilename());
        checkImage(multipartFile);

        try {
            blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);
        } catch (IOException e) {
            throw new InvalidResourceException("Lỗi khi tải lên ảnh: " + e);
        }
        Date date = new Date();
        Image image = Image.builder()
                .name(multipartFile.getOriginalFilename())
                .title("Ảnh "+ subCategory.getName())
                .description("Ảnh "+ subCategory.getName())
                .createdAt(date)
                .updatedAt(date)
                .url(blob.getBlobUrl())
                .subCategory(subCategory)
                .build();
        imageRepository.save(image);
        return mapper.map(image, ImageDTO.class);
    }

    @Override
    public boolean deleteImage(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Không tìm thấy ảnh")
        );
        BlobClient blob = blobContainerClient.getBlobClient(image.getName());
        blob.delete();
        imageRepository.delete(image);
        return true;
    }
    private void checkImage(MultipartFile multipartFile){
        List<Image> images = imageRepository.findAll();
        if(!images.isEmpty()){
            for (Image image : images
            ){
                if(image.getName().equalsIgnoreCase(multipartFile.getOriginalFilename())){
                    throw new InvalidResourceException("Ảnh đã tồn tại.");
                }
            }
        }
        if(!Objects.requireNonNull(multipartFile.getContentType()).startsWith("image")){
            throw new InvalidResourceException("File không hợp lệ");
        }
    }
}
