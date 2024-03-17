package com.example.imageproject.service;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.stereotype.Service;


@Service
public class ImageMagickService {



    public void resizeImage(String inputFilePath, String outputFilePath, int width, int height) {
        try {
            ConvertCmd cmd = new ConvertCmd();
            IMOperation op = new IMOperation();
            op.addImage(inputFilePath);
            op.resize(width, height);
            op.addImage(outputFilePath);
            cmd.run(op);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
