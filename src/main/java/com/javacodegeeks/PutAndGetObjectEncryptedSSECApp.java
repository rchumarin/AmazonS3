package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FileUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

public class PutAndGetObjectEncryptedSSECApp {
    private static final Logger LOGGER = Logger.getLogger(PutAndGetObjectEncryptedSSECApp.class.getName());

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length < 3) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <key> <file>");
            return;
        }
        String bucketName = args[0];
        String key = args[1];
        Path filePath = Paths.get(args[2]);
        AmazonS3 amazonS3 = AwsClientFactory.createClient();

        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256, new SecureRandom());
        SecretKey secretKey = generator.generateKey();
        SSECustomerKey sseKey = new SSECustomerKey(secretKey);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, filePath.toFile());
        putObjectRequest.setSSECustomerKey(sseKey);

        PutObjectResult result = amazonS3.putObject(putObjectRequest);

        LOGGER.info("Put file '" + filePath + "' under key " + key + " to bucket " + bucketName + " " + result.getSSEAlgorithm());

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        getObjectRequest.setSSECustomerKey(sseKey);
        S3Object s3Object = amazonS3.getObject(getObjectRequest);
        FileUtils.copyInputStreamToFile(s3Object.getObjectContent(), filePath.toFile());
    }
}
