package com.gcp.datastore.domain.core;

import com.gcp.datastore.domain.infrastructure.adapter.StorageAdapter;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.gcp.datastore.infrastructure.util.StorageUtils.convertToByteArray;

@RequiredArgsConstructor
@Slf4j
@Component
public class StorageFacade implements StorageAdapter {


    private final Storage storage;

    @Value("${bucketname}")
    private String bucketName;


    @Override
    public Mono<URL> uploadFile(FilePart filePart, String bucketName,String subdirectory) {
        final byte[] byteArray = convertToByteArray(filePart);

        final BlobId blobId = constructBlobId(bucketName, subdirectory, filePart.filename());

        return Mono.just(blobId)
                //Create the blobInfo
                .map(bId -> BlobInfo.newBuilder(blobId)
                        .build())
                //Upload the blob to GCS
                .doOnNext(blobInfo -> storage.create(blobInfo, byteArray))
                //Create a Signed "Path Style" URL to access the newly created Blob
                //Set the URL expiry to 10 Minutes
                .map(blobInfo -> createUrl(blobInfo, 999999, TimeUnit.DAYS));
    }

    @Override
    public Mono<Map<Object, Object>> bucketInfo(String bucketName) {
        Storage storage = StorageOptions.newBuilder().setProjectId("hsd-flow-develop").build().getService();

        // Select all fields. Fields can be selected individually e.g. Storage.BucketField.NAME
        Bucket bucket =
                storage.get(bucketName, Storage.BucketGetOption.fields(Storage.BucketField.values()));

        Map<Object, Object> info = new HashMap<>();
        info.put("BucketName: " , bucket.getName());
        info.put("DefaultEventBasedHold: " , bucket.getDefaultEventBasedHold());
        info.put("DefaultKmsKeyName: " , bucket.getDefaultKmsKeyName());
        info.put("Id: " , bucket.getGeneratedId());

        info.put("SelfLink: " , bucket.getSelfLink());
        info.put("StorageClass: " , bucket.getStorageClass().name());
        info.put("TimeCreated: " , bucket.getCreateTime());
        info.put("VersioningEnabled: " , bucket.versioningEnabled());
        info.put("labels: " , bucket.getLabels()==null?"":bucket.getLabels().entrySet());




        return Mono.just(info);

    }

    @Override
    public void createFolder(String name) {
        Storage storage = StorageOptions.newBuilder().setProjectId("hsd-flow-develop").build().getService();

        // Select all fields. Fields can be selected individually e.g. Storage.BucketField.NAME
        Bucket bucket =
                storage.get(bucketName, Storage.BucketGetOption.fields(Storage.BucketField.values()));
        Blob folderCreated = bucket.create(name + "/", "".getBytes());
        log.info("bucket criado "+folderCreated.getName());
    }


    private BlobId constructBlobId(String bucketName, @Nullable String subdirectory,
                                   String fileName) {
        return Optional.ofNullable(subdirectory)
                .map(s -> BlobId.of(bucketName, subdirectory + "/" + fileName))
                .orElse(BlobId.of(bucketName, fileName));
    }

    private URL createUrl(BlobInfo blobInfo,
                          int duration, TimeUnit timeUnit) {
        return storage
                .signUrl(blobInfo, duration, timeUnit, Storage.SignUrlOption.withPathStyle());
    }

}
