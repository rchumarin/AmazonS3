package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateBucketApp {
    private static final Logger LOGGER = Logger.getLogger(CreateBucketApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            LOGGER.log(Level.WARNING, "Please provide the following arguments: <bucket-name>");
            return;
        }
        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        CreateBucketRequest request = new CreateBucketRequest(args[0],
                AwsClientFactory.createRegionProvider().getRegion());
        Bucket bucket = amazonS3.createBucket(request);
        LOGGER.log(Level.INFO, "Created bucket " + bucket.getName() + ".");
    }
}
