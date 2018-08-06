package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.IOException;
import java.util.logging.Logger;

public class ListObjectsApp {
    private static final Logger LOGGER = Logger.getLogger(ListObjectsApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <delimiter> <prefix>");
            return;
        }
        String bucketName = args[0];
        String delimiter = args[1];
        String prefix = args[2];

        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
        listObjectsRequest.setBucketName(bucketName);
        listObjectsRequest.setDelimiter(delimiter);
        listObjectsRequest.setPrefix(prefix);
        ListObjectsV2Result result = amazonS3.listObjectsV2(listObjectsRequest);

        for (S3ObjectSummary summary : result.getObjectSummaries()) {
            LOGGER.info(summary.getKey() + ":" + summary.getSize());
        }
    }
}
