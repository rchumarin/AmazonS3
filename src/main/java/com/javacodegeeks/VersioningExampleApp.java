package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VersioningExampleApp {
    private static final Logger LOGGER = Logger.getLogger(VersioningExampleApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <key>");
            return;
        }
        String bucketName = args[0];
        String key = args[1];
        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        Charset charset = Charset.forName("UTF-8");

        byte[] bytes = "Version 1".getBytes(charset);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,
                new ByteArrayInputStream(bytes), new ObjectMetadata());
        amazonS3.putObject(putObjectRequest);

        bytes = "Version 2".getBytes(charset);
        putObjectRequest = new PutObjectRequest(bucketName, key,
                new ByteArrayInputStream(bytes), new ObjectMetadata());
        amazonS3.putObject(putObjectRequest);

        List<String> versionIds = new ArrayList<>();
        ListVersionsRequest listVersionsRequest = new ListVersionsRequest();
        listVersionsRequest.setBucketName(bucketName);
        VersionListing versionListing;
        do {
            versionListing = amazonS3.listVersions(listVersionsRequest);
            for (S3VersionSummary objectSummary : versionListing.getVersionSummaries()) {
                LOGGER.info(objectSummary.getKey() + ": " + objectSummary.getVersionId());
                if (objectSummary.getKey().equals(key)) {
                    versionIds.add(objectSummary.getVersionId());
                }
            }
            listVersionsRequest.setKeyMarker(versionListing.getNextKeyMarker());
            listVersionsRequest.setVersionIdMarker(versionListing.getNextVersionIdMarker());
        } while (versionListing.isTruncated());

        for (String versionId : versionIds) {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            getObjectRequest.setVersionId(versionId);
            S3Object s3Object = amazonS3.getObject(getObjectRequest);
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy(s3Object.getObjectContent(), stringWriter, charset);
            LOGGER.info(versionId + ": " + stringWriter.toString());
        }

        for (String versionId : versionIds) {
            DeleteVersionRequest dvr = new DeleteVersionRequest(bucketName, key, versionId);
            amazonS3.deleteVersion(dvr);
            LOGGER.info("Deleted version " + versionId + ".");
        }
    }
}
