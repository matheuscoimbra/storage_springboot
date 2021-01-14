package com.gcp.datastore.domain.application;

import com.gcp.datastore.domain.core.StorageFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class StorageRouter {

    @Bean
    public RouterFunction<ServerResponse> router(StorageFacade storageFacade){
        return RouterFunctions
                .route(GET("/download/{subdirectory}/{object}").and(accept(MediaType.APPLICATION_JSON)),storageFacade::getFileResponseHandler);
    }
}
