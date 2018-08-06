package com.javacodegeeks;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MultipartUploadTransferManagerApp {
    private static final Logger LOGGER = Logger.getLogger(MultipartUploadTransferManagerApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <key> <file>");
            return;
        }
        String bucketName = args[0];
        String key = args[1];
        Path filePath = Paths.get(args[2]);

        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        TransferManagerBuilder transferManagerBuilder = TransferManagerBuilder.standard();
        transferManagerBuilder.setS3Client(amazonS3);
        transferManagerBuilder.setExecutorFactory(() -> Executors.newFixedThreadPool(4));
        TransferManager tm = transferManagerBuilder.build();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, filePath.toFile());
        Upload upload = tm.upload(putObjectRequest);
        LOGGER.info("Started upload of " + filePath.toString());

        upload.addProgressListener((ProgressEvent progressEvent) -> {
            LOGGER.info("Progress: " + progressEvent.getBytes());
        });

        try {
            upload.waitForCompletion();
            LOGGER.info("Finished upload of " + filePath.toString());
        } catch (InterruptedException e) {
            LOGGER.warning("Failed to upload file: " + e.getLocalizedMessage());
        }

        tm.shutdownNow();
    }
}
