package com.example.imageproject.service;

import com.example.imageproject.config.AESEncryption;
import com.example.imageproject.domain.CustomUser;
import com.example.imageproject.domain.Image;
import com.example.imageproject.dto.ImageInfo;
import com.example.imageproject.exception.IOExceptionImpl;
import com.example.imageproject.exception.ImageNotBelongsToTheUserException;
import com.example.imageproject.exception.ImageNotFoundException;
import com.example.imageproject.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class ImageService {

    private CustomUserService customUserService;
    private ImageRepository imageRepository;
    private AESEncryption aesEncryption;
    private ImageMagickService imageMagickService;

    private static final String UPLOAD_SUCCESS = "Image uploaded successfully.";
    private static final String UPLOAD_EMPTY = "Please upload a file.";
    private static final String CONTENT_TYPE_JPG = "image/jpeg";
    private static final String CONTENT_TYPE_PNG = "image/png";
    private static final String CONTENT_TYPE_ALLOWED = "Only JPG and PNG formats are allowed.";
    private static final String IMAGE_DIMENSIONS = "Image dimensions exceed the limits (5000x5000), so it is converted to it.";


    @Autowired
    public ImageService(CustomUserService customUserService, ImageRepository imageRepository, AESEncryption aesEncryption, ImageMagickService imageMagickService) {
        this.customUserService = customUserService;
        this.imageRepository = imageRepository;
        this.aesEncryption = aesEncryption;
        this.imageMagickService = imageMagickService;
    }


    public String uploadOnePicture(MultipartFile file, String username) {
        String message = "";
        try {
            String contentType = file.getContentType();
            if (file.isEmpty() || contentType == null) {
                message = UPLOAD_EMPTY;
            } else if (!contentType.equals(CONTENT_TYPE_JPG) && !contentType.equals(CONTENT_TYPE_PNG)) {
                message = CONTENT_TYPE_ALLOWED;
            } else {
                BufferedImage imageSize = ImageIO.read(file.getInputStream());
                if (imageSize.getWidth() <= 5000 && imageSize.getHeight() <= 5000) {
                    byte[] imageData = file.getBytes();
                    saveAndEncryptImage(imageData, username);
                    message = UPLOAD_SUCCESS;
                } else {
                    File convertedFile = File.createTempFile("converted", ".jpg");
                    file.transferTo(convertedFile);
                    imageMagickService.resizeImage(convertedFile.getAbsolutePath(), "output.jpg", 5000, 5000);
                    message = IMAGE_DIMENSIONS;
                }
            }
            return message;
        } catch (Exception e) {
            throw new IOExceptionImpl(username);
        }
    }


    public String saveImagesFromDirectory(String directoryPath, String username) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        try {
            String message = "Result:";

            if (files != null) {
                for (File file : files) {
                    String contentType = Files.probeContentType(file.toPath());
                    if (file.length() == 0 || contentType == null) {
                        message += "\n" + UPLOAD_EMPTY;
                    } else if (!contentType.equals(CONTENT_TYPE_JPG) && !contentType.equals(CONTENT_TYPE_PNG)) {
                        message += "\n" + CONTENT_TYPE_ALLOWED;
                    } else {
                        BufferedImage imageSize = ImageIO.read(new FileInputStream(file));
                        if (imageSize.getWidth() <= 5000 && imageSize.getHeight() <= 5000) {
                            byte[] imageData = Files.readAllBytes(file.toPath());
                            saveAndEncryptImage(imageData, username);
                            message = message + "\n" + UPLOAD_SUCCESS;
                        } else {
                            imageMagickService.resizeImage(file.getPath(), "output.jpg", 5000, 5000);
                            message = "\n" + IMAGE_DIMENSIONS;
                        }
                    }
                }
            }
            return message;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


    public void saveAndEncryptImage(byte[] imageData, String username) {
        try {
            CustomUser customUser = customUserService.findCustomUserByUsername(username);
            Image image = new Image();
            image.setData(aesEncryption.encrypt(imageData));
            image.setCustomUser(customUser);
            imageRepository.save(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ImageInfo getImageById(Long imageId, String username) {
        CustomUser customUser = customUserService.findCustomUserByUsername(username);
        try {
            Image image = findImageByIdInRepository(imageId);
            ImageInfo imageInfo = new ImageInfo();
            if (customUser.getImages().contains(image)) {
                imageInfo.setData(aesEncryption.decrypt(image.getData()));
                return imageInfo;
            } else {
                throw new ImageNotBelongsToTheUserException(username);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Image findImageByIdInRepository(Long imageId) {
        Optional<Image> imageOptional = imageRepository.findById(imageId);
        if (imageOptional.isEmpty()) {
            throw new ImageNotFoundException(imageId);
        }
        return imageOptional.get();
    }


    public byte[] getImages(String username) {
        CustomUser customUser = customUserService.findCustomUserByUsername(username);
        try {
            ImageInfo imageInfo = new ImageInfo();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            for (int i = 0; i < customUser.getImages().size(); i++) {
                Image image = customUser.getImages().get(i);
                imageInfo.setData(aesEncryption.decrypt(image.getData()));
                zipOutputStream.putNextEntry(new ZipEntry(image.getId() + ".jpg"));
                zipOutputStream.write(imageInfo.getData());
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            byte[] zipData = outputStream.toByteArray();
            return zipData;
        } catch (Exception e) {
            throw new IOExceptionImpl(username);
        }
    }

}
