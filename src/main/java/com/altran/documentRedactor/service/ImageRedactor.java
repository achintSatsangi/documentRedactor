package com.altran.documentRedactor.service;

import com.altran.documentRedactor.pojo.WordObject;
import com.google.cloud.vision.v1.BoundingPoly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ImageRedactor {

    private static final Logger LOG = LogManager.getLogger(ImageRedactor.class);

    public byte[] getRedactedImage(MultipartFile file, List<WordObject> redactedWords) {
        try {
            BufferedImage bi = ImageIO.read(file.getInputStream());
            Graphics2D ig2 = bi.createGraphics();
            ig2.setPaint(Color.black);
            for (WordObject word: redactedWords) {
                BoundingPoly boundingBox = word.getWord().getBoundingBox();
                ig2.fill(new Rectangle(boundingBox.getVertices(0).getX(),
                                        boundingBox.getVertices(0).getY(),
                        boundingBox.getVertices(2).getX() - boundingBox.getVertices(0).getX(),
                        boundingBox.getVertices(2).getY() - boundingBox.getVertices(0).getY()));
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (Exception e) {
            LOG.error("While redacting image", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
