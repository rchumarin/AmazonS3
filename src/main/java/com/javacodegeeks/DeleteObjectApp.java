package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.logging.Logger;

public class DeleteObjectApp {
    private static final Logger LOGGER = Logger.getLogger(DeleteObjectApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <key>");
            return;
        }
        String bucketName = args[0];
        String key = args[1];

        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3.deleteObject(deleteObjectRequest);
        LOGGER.info("Deleted object with key '" + key + "'.");
    }
}
