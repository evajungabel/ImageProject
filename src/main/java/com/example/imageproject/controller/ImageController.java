package com.example.imageproject.controller;


import com.example.imageproject.domain.Image;
import com.example.imageproject.dto.ImageInfo;
import com.example.imageproject.exception.AuthenticationExceptionImpl;
import com.example.imageproject.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@Slf4j
public class ImageController {

    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @PostMapping(value = "/files", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's image is saved by customer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<String> uploadOnePicture(@RequestParam("file") MultipartFile file) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, POST image /api/files, with: " + userDetails.getUsername());
        String message = imageService.uploadOnePicture(file, userDetails.getUsername());
        log.info("POST data image of repository from /api/files, with: " + userDetails.getUsername());
        return ResponseEntity.ok(message);
    }


    @PostMapping(value = "/files/{username}", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customers's image by admin.")
    @ApiResponse(responseCode = "201", description = "Customer's image is saved by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> uploadPictureFromAdmin(@PathVariable("username") String username, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Http request, POST image /api/files/{username}, with: " + username);
        String message = imageService.uploadOnePicture(file, username);
        log.info("POST data image from repository/api/files/{username}, with: " + username);
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/files/upload-batch", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's images are saved by customer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<String> uploadMorePicture(@RequestParam("directory") String directoryPath) throws AuthenticationExceptionImpl {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, POST image /api/files/upload-batch, with: " + userDetails.getUsername());
        String message = imageService.saveImagesFromDirectory(directoryPath, userDetails.getUsername());
        log.info("POST data images of repository from /api/files/upload-batch, with: " + userDetails.getUsername());
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/files/upload-batch/{username}", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's images are saved by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> uploadMorePictureFromAdmin(@PathVariable("username") String username, @RequestParam("directory") String directoryPath) throws AuthenticationExceptionImpl {
        log.info("Http request, POST image /api/files/upload-batch/{username}, with: " + username);
        String message = imageService.saveImagesFromDirectory(directoryPath, username);
        log.info("POST data images of repository from /api/files/upload-batch/{username}, with: " + username);
        return ResponseEntity.ok(message);
    }


    @GetMapping(value = "/file/{fileName}")
    @Operation(summary = "Downloading customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's image is downloaded by customer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<ByteArrayResource> downloadOnePicture(@PathVariable Long fileName) throws AuthenticationExceptionImpl {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, GET data image /api/file/{fileName}, with: " + userDetails.getUsername());
        ImageInfo imageInfo = imageService.getImageById(fileName, userDetails.getUsername());

        if (imageInfo == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(imageInfo.getData());
        log.info("GET data image of repository from /api/file/{fileName}, with: " + userDetails.getUsername());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".jpg\"")
                .body(resource);
    }

    @GetMapping(value = "/file/{fileName}/{username}")
    @Operation(summary = "Downloading customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's image is downloaded by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ByteArrayResource> downloadOnePicture(@PathVariable Long imageId, @PathVariable String username) throws AuthenticationExceptionImpl {
        log.info("Http request, GET data image /api/file/{fileName}/{username}, with: " + username);
        ImageInfo imageInfo = imageService.getImageById(imageId, username);

        if (imageInfo == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(imageInfo.getData());
        log.info("GET data image of repository from /api/file/{fileName}/{username}, with: " + username);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageId + ".jpg\"")
                .body(resource);
    }

    @GetMapping("/file")
    @Operation(summary = "Downloading customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's ZIP file is downloaded by customer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<byte[]> downloadZIPFile() throws AuthenticationExceptionImpl {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, GET data image /api/file, with: " + userDetails.getUsername());
        byte[] zipData = imageService.getImages(userDetails.getUsername());
        log.info("GET data image of repository from /api/file, with: " + userDetails.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "images.zip");

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
    }


    @GetMapping(value = "/file/{username}", consumes = {"multipart/form-data"})
    @Operation(summary = "Downloading customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's ZIP file is downloaded by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<byte[]> downloadZIPFileByAdmin(@PathVariable String username) throws AuthenticationExceptionImpl {
        log.info("Http request, GET data image /api/file/{username}, with: " + username);
        byte[] zipData = imageService.getImages(username);
        log.info("GET data image of repository from /api/file, with: " + username);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"images.zip\"")
                .body(zipData);
    }
}
