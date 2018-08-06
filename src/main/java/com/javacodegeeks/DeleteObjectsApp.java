package com.javacodegeeks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DeleteObjectsApp {
    private static final Logger LOGGER = Logger.getLogger(DeleteObjectsApp.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            LOGGER.warning("Please provide the following parameters: <bucket-name> <key> [key...]");
            return;
        }
        String bucketName = args[0];
        List<DeleteObjectsRequest.KeyVersion> keyVersionList = new ArrayList<>(args.length - 1);
        for (int i = 1; i < args.length; i++) {
            DeleteObjectsRequest.KeyVersion keyVersion = new DeleteObjectsRequest.KeyVersion(args[i]);
            keyVersionList.add(keyVersion);
        }

        AmazonS3 amazonS3 = AwsClientFactory.createClient();
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
        deleteObjectsRequest.setKeys(keyVersionList);
        List<DeleteObjectsResult.DeletedObject> deletedObjects;
        try {
            DeleteObjectsResult result = amazonS3.deleteObjects(deleteObjectsRequest);
            deletedObjects = result.getDeletedObjects();
        } catch (MultiObjectDeleteException e) {
            deletedObjects = e.getDeletedObjects();
            List<MultiObjectDeleteException.DeleteError> errors = e.getErrors();
            for (MultiObjectDeleteException.DeleteError error : errors) {
                LOGGER.info("Failed to delete object with key '" + error.getKey() + "': " + e.getLocalizedMessage());
            }
        }
        for (DeleteObjectsResult.DeletedObject deletedObject : deletedObjects) {
            LOGGER.info("Deleted object with key '" + deletedObject.getKey() + "'.");
        }
    }
}
