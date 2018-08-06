package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

public class PutObjectApp {
    private static final Logger LOGGER = Logger.getLogger(PutObjectApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <key> <file>");
            return;
        }
        String bucketName = args[0];
        String key = args[1];
        Path filePath = Paths.get(args[2]);
        AmazonS3 amazonS3 = AwsClientFactory.createClient();

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, filePath.toFile());

//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
//        putObjectRequest.setMetadata(objectMetadata);

//        SSEAwsKeyManagementParams awsKMParams = new SSEAwsKeyManagementParams();
//        putObjectRequest.setSSEAwsKeyManagementParams(awsKMParams);

        putObjectRequest.setStorageClass(StorageClass.StandardInfrequentAccess);
        putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(new Tag("archive", "true"))));
        PutObjectResult result = amazonS3.putObject(putObjectRequest);
        LOGGER.info("Put file '" + filePath + "' under key " + key + " to bucket " + bucketName + " (SSE: " + result.getSSEAlgorithm() + ")");
    }
}
