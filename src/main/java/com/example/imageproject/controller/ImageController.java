package com.example.imageproject.controller;


import com.example.imageproject.exception.AuthenticationExceptionImpl;
import com.example.imageproject.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class ImageUploadController {

    private ImageService imageService;

    @Autowired
    public ImageUploadController(ImageService imageService) {
        this.imageService = imageService;
    }


    //TODO exception
    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's image is saved by customer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<String> uploadOnePicture(@RequestParam("file") MultipartFile file) throws AuthenticationExceptionImpl {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, POST image /api/files, with: " + userDetails.getUsername());

        try {
            String message = imageService.uploadOnePicture(file, userDetails.getUsername());
            log.info("POST data image of repository from /api/files, with: " + userDetails.getUsername());
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //TODO exception
    @PostMapping(value = "/{username}", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customers's image by admin.")
    @ApiResponse(responseCode = "201", description = "Customer's image is saved by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> uploadPictureFromAdmin(@PathVariable("username") String username, @RequestParam("file") MultipartFile file) throws AuthenticationExceptionImpl {

        log.info("Http request, POST image /api/files/{username}, with: " + username);

        try {
            String message = imageService.uploadOnePicture(file, username);
            log.info("POST data image from repository/api/files/{username}, with: " + username);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/upload-batch", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's images are saved by customer.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<String> uploadMorePicture(@RequestParam("directory") String directoryPath) throws AuthenticationExceptionImpl {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Http request, POST image /api/files/upload-batch, with: " + userDetails.getUsername());

        try {
            String message = imageService.saveImagesFromDirectory(directoryPath, userDetails.getUsername());
            log.info("POST data images of repository from /api/files/upload-batch, with: " + userDetails.getUsername());
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images");
        }
    }

    @PostMapping(value = "/upload-batch/{username}", consumes = {"multipart/form-data"})
    @Operation(summary = "Saving customer's image")
    @ApiResponse(responseCode = "201", description = "Customer's images are saved by admin.")
    @SecurityRequirement(name = "basicAuth")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> uploadMorePictureFromAdmin(@PathVariable("username") String username, @RequestParam("directory") String directoryPath) throws AuthenticationExceptionImpl {
        log.info("Http request, POST image /api/files/upload-batch/{username}, with: " + username);
        try {
            imageService.saveImagesFromDirectory(directoryPath, username);
            log.info("POST data images of repository from /api/files/upload-batch/{username}, with: " + username);
            return ResponseEntity.ok("Images uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images");
        }
    }



//
//    @PostMapping("/profile/uploadurl")
//    @Operation(summary = "Saving customer's image uploaded as URL by customer")
//    @ApiResponse(responseCode = "201", description = "Customer's image uploaded as URL is saved by customer.")
//    @SecurityRequirement(name = "basicAuth")
//    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
//    public ResponseEntity<Map<String, Object>> uploadImageProfileFromURL(@RequestParam("url") String url) throws AuthenticationExceptionImpl {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info("Http request, POST imageURL /api/cloudinary/profile/uploadurl, with: " + userDetails.getUsername());
//        Map<String, Object> data = this.cloudinaryImageService.uploadProfileFromURL(url, userDetails.getUsername());
//        log.info("POST data imageURL of repository from /api/cloudinary/profile/uploadurl, with: " + userDetails.getUsername());
//        cloudinaryImageService.getProfileURL(data);
//        return new ResponseEntity<>(data, HttpStatus.CREATED);
//    }
//
//    @PostMapping("/profile/uploadurl/{username}")
//    @Operation(summary = "Saving customer's image uploaded as URL by admin")
//    @ApiResponse(responseCode = "201", description = "Property's image uploaded as URL is saved by admin.")
//    @SecurityRequirement(name = "basicAuth")
//    @Secured({"ROLE_ADMIN"})
//    public ResponseEntity<Map<String, Object>> uploadImageProfileFromURL(@PathVariable("username") String username, @RequestParam("url") String url) throws AuthenticationExceptionImpl {
//        log.info("Http request, POST imageURL /api/cloudinary/profile/uploadurl/{username}, with: " + username);
//        Map<String, Object> data = this.cloudinaryImageService.uploadProfileFromURL(url, username);
//        log.info("POST data imageURL from repository/api/cloudinary/profile/uploadurl/{username}, with: " + username);
//        cloudinaryImageService.getProfileURL(data);
//        return new ResponseEntity<>(data, HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    @Operation(summary = "Notification URL about saved property's image")
//    @ApiResponse(responseCode = "201", description = "Notification URL about saved property's image is sent.")
//    public ResponseEntity<String> notificationImage() {
//        log.info("Http request, GET upload notification /api/cloudinary");
//        return new ResponseEntity<>("The file is uploaded!", HttpStatus.OK);
//    }
//
//
//
//
//    @DeleteMapping("/profile")
//    @Operation(summary = "Deleting customer's image form cloudinary by customer")
//    @ApiResponse(responseCode = "200", description = "Customer's image is deleted from cloudinary by customer.")
//    @SecurityRequirement(name = "basicAuth")
//    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_AGENT"})
//    public ResponseEntity<Map<String, Object>> deleteProfileImage(@RequestParam("customUserImageURLId") Long customUserImageURLId) throws AuthenticationExceptionImpl {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info("Http request, DELETE image /api/cloudinary/profile, with: " + userDetails.getUsername() + "customUserImageURLId: " + customUserImageURLId);
//        Map<String, Object> data = this.cloudinaryImageService.deleteProfileImage(userDetails.getUsername(), customUserImageURLId);
//        log.info("DELETE data image of repository from /api/cloudinary/profile, with: " + userDetails.getUsername() + "customUserImageURLId: " + customUserImageURLId);
//        return new ResponseEntity<>(data, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/profile/{username}")
//    @Operation(summary = "Deleting customer's image form cloudinary by admin")
//    @ApiResponse(responseCode = "200", description = "Customer's image is deleted from cloudinary by admin.")
//    @SecurityRequirement(name = "basicAuth")
//    @Secured({"ROLE_ADMIN"})
//    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable("username") String username, @RequestParam("customUserImageURLId") Long customUserImageURLId) throws AuthenticationExceptionImpl {
//        log.info("Http request, DELETE image /api/cloudinary/profile/{username}, with: " + username + "customUserImageURLId: " + customUserImageURLId);
//        Map<String, Object> data = this.cloudinaryImageService.deleteProfileImage(username, customUserImageURLId);
//        log.info("DELETE data image of repository from /api/cloudinary/profile/{username}, with: " + username + "customUserImageURLId: " + customUserImageURLId);
//        return new ResponseEntity<>(data, HttpStatus.OK);
//    }
}
