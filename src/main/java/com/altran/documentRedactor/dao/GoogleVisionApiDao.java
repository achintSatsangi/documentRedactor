package com.altran.documentRedactor.dao;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GoogleVisionApiDao {

    private static final Logger LOG = LogManager.getLogger(GoogleVisionApiDao.class);

    public BatchAnnotateImagesResponse getResponse(MultipartFile file) throws Exception {
        //System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "/Users/achintsatsangi/goolgeCloudAuthentication.json");
        // Instantiates a client
        ImageAnnotatorClient vision = ImageAnnotatorClient.create();
        ByteString imgBytes = ByteString.copyFrom(file.getBytes());

        // Builds the image annotation request
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();

        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        Feature feat1 = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
        Feature feat2 = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .addFeatures(feat1)
                .addFeatures(feat2)
                .setImage(img)
                .build();
        requests.add(request);

        // Performs label detection on the image file
        return vision.batchAnnotateImages(requests);
    }

}
