package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;

import java.io.IOException;
import java.util.logging.Logger;

public class CopyObjectApp {
    private static final Logger LOGGER = Logger.getLogger(CopyObjectApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            LOGGER.warning("Please provide the following parameters: <src-bucket-name> <src-key> <dst-bucket-name> <dst-key>");
            return;
        }
        String srcBucketName = args[0];
        String srcKey = args[1];
        String dstBucketName = args[2];
        String dstKey = args[3];

        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(srcBucketName, srcKey, dstBucketName, dstKey);
        CopyObjectResult result = amazonS3.copyObject(copyObjectRequest);
        String eTag = result.getETag();
        LOGGER.info("Copied object from bucket " + srcBucketName + " with key " + srcKey + " to bucket"
                + dstBucketName + " with key " + dstKey + ", eTag: " + eTag);
    }
}
