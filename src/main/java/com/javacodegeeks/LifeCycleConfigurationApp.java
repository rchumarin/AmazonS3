package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LifeCycleConfigurationApp {
    private static final Logger LOGGER = Logger.getLogger(LifeCycleConfigurationApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            LOGGER.log(Level.WARNING, "Please provide the following arguments: <bucket-name>");
            return;
        }
        String bucketName = args[0];

        BucketLifecycleConfiguration.Rule rule =
                new BucketLifecycleConfiguration.Rule()
                        .withId("Transfer to IA, then GLACIER, then remove")
                        .withFilter(new LifecycleFilter(
                                new LifecycleTagPredicate(new Tag("archive", "true"))))
                        .addTransition(new BucketLifecycleConfiguration.Transition()
                                .withDays(30)
                                .withStorageClass(StorageClass.StandardInfrequentAccess))
                        .addTransition(new BucketLifecycleConfiguration.Transition()
                                .withDays(365)
                                .withStorageClass(StorageClass.Glacier))
                        .withExpirationInDays(365 * 5)
                        .withStatus(BucketLifecycleConfiguration.ENABLED);
        BucketLifecycleConfiguration conf =
                new BucketLifecycleConfiguration()
                        .withRules(rule);

        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        amazonS3.setBucketLifecycleConfiguration(bucketName, conf);
    }
}
