package com.gcp.datastore.domain.core.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;

@Builder
@Data
public class MediaDTO {

    private String name;
    private String contentType;
}
