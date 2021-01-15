package com.gcp.datastore.domain.application;

import com.gcp.datastore.domain.core.StorageFacade;
import com.gcp.datastore.domain.core.model.MediaDTO;
import com.google.rpc.context.AttributeContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping()
@Slf4j
public class StorageResource {


    private final StorageFacade storage;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<MediaDTO> uploadFile(@RequestPart("file") FilePart filePart, @RequestParam("subdirectory") String subdirectory) {

        return storage.uploadFile(filePart, subdirectory );
    }

    @GetMapping(value = "/bucket/info")
    public Mono<?> uploadFile(@RequestParam("bucket") String name) {

        return storage.bucketInfo(name);
    }

   /* @GetMapping(value = "/download/{subdirectory}/{object}",produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<byte[]> getFile(@PathVariable("subdirectory") String subdirectory, @PathVariable("object") String object) {

        return storage.getFile(subdirectory,object);
    }*/

    @DeleteMapping(value = "/object")
    public ResponseEntity<?> delete(@RequestParam("file") String file){
        var deleted = storage.deleteObject(file);
        return deleted?ResponseEntity.ok().build():ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/directory/all",produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<MediaDTO> getFile(@RequestParam("directoryPrefix") String subdirectory) {

        return storage.getFilesFromDirectory(subdirectory);
    }

    @PostMapping(value = "/bucket/create")
    public ResponseEntity<Object> createBucket(@RequestParam("bucket") String name) {
        storage.createFolder(name);
        return ResponseEntity.ok().build();
    }




}


