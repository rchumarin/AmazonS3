package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteBucketApp {
    private static final Logger LOGGER = Logger.getLogger(DeleteBucketApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            LOGGER.log(Level.WARNING, "Please provide the following arguments: <bucket-name>");
            return;
        }
        String bucketName = args[0];
        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucketName);
        amazonS3.deleteBucket(deleteBucketRequest);
        LOGGER.log(Level.INFO, "Deleted bucket " + bucketName + ".");
    }
}
