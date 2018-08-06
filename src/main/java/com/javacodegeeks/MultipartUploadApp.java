package com.javacodegeeks;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MultipartUploadApp {
    private static final Logger LOGGER = Logger.getLogger(MultipartUploadApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <key> <file>");
            return;
        }
        String bucketName = args[0];
        String key = args[1];
        Path filePath = Paths.get(args[2]);
        AmazonS3 amazonS3 = AwsClientFactory.createClient();

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult initResponse = amazonS3.initiateMultipartUpload(initRequest);
        LOGGER.info("Initiated upload with id: " + initResponse.getUploadId());

        long fileSize = Files.size(filePath);
        long partSize = 5 * 1024 * 1024; // 5MB
        long filePosition = 0;
        List<PartETag> partETags = new ArrayList<>();

        try {
            int part = 1;
            while (filePosition < fileSize) {
                partSize = Math.min(partSize, fileSize - filePosition);

                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(key)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(part) // parts start with 1
                        .withFileOffset(filePosition)
                        .withFile(filePath.toFile())
                        .withPartSize(partSize);
                UploadPartResult uploadPartResult = amazonS3.uploadPart(uploadRequest);
                partETags.add(uploadPartResult.getPartETag());
                LOGGER.info("Uploaded part: " + part);

                filePosition += partSize;
                part++;
            }

            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName,
                    key, initResponse.getUploadId(), partETags);
            amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (SdkClientException e) {
            LOGGER.warning("Failed to upload file: " + e.getMessage());
            amazonS3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, key, initResponse.getUploadId()));
            LOGGER.info("Aborted upload.");
        }
    }
}
