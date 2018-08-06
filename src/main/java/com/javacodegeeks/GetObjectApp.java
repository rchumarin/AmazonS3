package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class GetObjectApp {
    private static final Logger LOGGER = Logger.getLogger(GetObjectApp.class.getName());

    public static void main(String[] args) throws IOException {
//        if (args.length < 3) {
//            LOGGER.warning("Please provide the following parameters: <bucket-name> <key> <file>");
//            return;
//        }

        String bucketName = "my-first-s3-bucket-9906d1e5-f435-4a16-87d8-ade1c75a0ab8";
        String key = "MyObjectKey";
        Path filePath = Paths.get("image.jpg");
        AmazonS3 amazonS3 = AwsClientFactory.createClient();

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        S3Object s3Object = amazonS3.getObject(getObjectRequest);

        LOGGER.info("Downloaded S3 object: " + s3Object.getKey() + " from bucket " +s3Object.getBucketName());

        S3ObjectInputStream stream = s3Object.getObjectContent();
        FileUtils.copyInputStreamToFile(stream, filePath.toFile());
    }
}
