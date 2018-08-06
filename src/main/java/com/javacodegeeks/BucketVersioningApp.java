package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BucketVersioningApp {
    private static final Logger LOGGER = Logger.getLogger(BucketVersioningApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            LOGGER.log(Level.WARNING, "Please provide the following arguments: <bucket-name> <versioning-status>");
            return;
        }
        String bucketName = args[0];
        String versioningStatus = args[1];
        AmazonS3 amazonS3 = AwsClientFactory.createClient();

        if (!BucketVersioningConfiguration.ENABLED.equals(versioningStatus)
                && !BucketVersioningConfiguration.SUSPENDED.equals(versioningStatus)) {
            LOGGER.log(Level.SEVERE, "Please provide a valid versioning status.");
            return;
        }

        BucketVersioningConfiguration conf = new BucketVersioningConfiguration();
        conf.setStatus(versioningStatus);

        SetBucketVersioningConfigurationRequest request =
                new SetBucketVersioningConfigurationRequest(bucketName, conf);
        amazonS3.setBucketVersioningConfiguration(request);

        conf = amazonS3.getBucketVersioningConfiguration(bucketName);
        LOGGER.info("Bucket " + bucketName + " has this versioning status: " + conf.getStatus());
    }
}
