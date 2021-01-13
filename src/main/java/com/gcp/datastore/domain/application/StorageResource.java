package com.gcp.datastore.domain.application;

import com.gcp.datastore.domain.core.StorageFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URL;

@RequiredArgsConstructor
@RestController
@RequestMapping("storage")
@Slf4j
public class StorageResource {


    private final StorageFacade storage;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<URL> uploadFile(@RequestPart("file") FilePart filePart,@RequestParam("bucket") String name, @RequestParam("subdirectory") String subdirectory) {

        return storage.uploadFile(filePart, name,subdirectory );
    }

    @GetMapping(value = "/bucket/info")
    public Mono<?> uploadFile(@RequestParam("bucket") String name) {

        return storage.bucketInfo(name);
    }

    @PostMapping(value = "/bucket/create")
    public ResponseEntity<Object> createBucket(@RequestParam("bucket") String name) {
        storage.createFolder(name);
        return ResponseEntity.ok().build();
    }




}


