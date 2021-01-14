package com.gcp.datastore.domain.core;

import com.gcp.datastore.domain.core.model.MediaDTO;
import com.gcp.datastore.domain.infrastructure.adapter.StorageAdapter;
import com.gcp.datastore.infrastructure.util.StorageUtils;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.*;

import static com.gcp.datastore.infrastructure.util.StorageUtils.convertToByteArray;

@RequiredArgsConstructor
@Slf4j
@Component
public class StorageFacade implements StorageAdapter {


    private final Storage storage;

    @Value("${bucketname}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;


    @Override
    public Mono<MediaDTO> uploadFile(FilePart filePart, String subdirectory) {
        final byte[] byteArray = convertToByteArray(filePart);

        checkFileExtension(filePart.filename());

        final String contenType = filePart.headers().getContentType().getType()+"/"+filePart.headers().getContentType().getSubtype();
        final BlobId blobId = constructBlobId(bucketName, subdirectory, filePart.filename());

        BlobInfo blobInfo =
                storage.create(
                        BlobInfo
                                .newBuilder(blobId).setContentType(contenType)
                                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                                .build(),byteArray

                );


        return Mono.just( createUrl( blobInfo,contenType));
    }

    private MediaDTO createUrl(BlobInfo blobInfo, String contenType) {
        return MediaDTO.builder().name("/download/"+blobInfo.getName()).contentType(contenType).build();
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
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Bucket bucket =
                storage.get(bucketName, Storage.BucketGetOption.fields(Storage.BucketField.values()));
        Blob folderCreated = bucket.create(name + "/", "".getBytes());
        log.info("bucket criado "+folderCreated.getName());
    }


    //public Mono<ServerResponse> getFileResponseHandler(ServerRequest serverRequest)

    @Override
    public Mono<byte[]> getFile(String subdirectory, String objectName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Blob blob = storage.get(BlobId.of(bucketName, subdirectory+"/"+objectName));

        return Mono.just(blob.getContent());
    }


    private BlobId constructBlobId(String bucketName, @Nullable String subdirectory,
                                   String fileName) {
        return Optional.ofNullable(subdirectory)
                .map(s -> BlobId.of(bucketName, subdirectory + "/" + fileName))
                .orElse(BlobId.of(bucketName, fileName));
    }

    private void checkFileExtension(String fileName) {
        if (fileName != null && !fileName.isEmpty() && fileName.contains(".")) {
            String[] allowedExt = {".jpg", ".jpeg", ".png", ".gif",".pdf"};
            for (String ext : allowedExt) {
                if (fileName.endsWith(ext)) {
                    return;
                }
            }
            throw new RuntimeException("file must be an image");
        }
    }



}
