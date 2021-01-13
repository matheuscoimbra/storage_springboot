package com.gcp.datastore.domain.infrastructure.adapter;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Map;

public interface StorageAdapter {

    Mono<URL> uploadFile(FilePart filePart, String bucketName, String subdirectory);

    Mono<Map<Object,Object>> bucketInfo(String bucketName);

    void createFolder(String name);
}
