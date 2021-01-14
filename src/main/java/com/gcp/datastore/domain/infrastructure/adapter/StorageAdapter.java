package com.gcp.datastore.domain.infrastructure.adapter;

import com.gcp.datastore.domain.core.model.MediaDTO;
import com.google.cloud.storage.Blob;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface StorageAdapter {

    Mono<MediaDTO> uploadFile(FilePart filePart, String subdirectory);

    Mono<Map<Object,Object>> bucketInfo(String bucketName);

    void createFolder(String name);

    Blob getFile(String subdirectory, String objectName);
}
