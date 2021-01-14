package com.gcp.datastore.infrastructure.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class StorageUtils {

    @SneakyThrows
    public static byte[] convertToByteArray(FilePart filePart) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            filePart.content()
                    .subscribe(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        log.trace("readable byte count:" + dataBuffer.readableByteCount());
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        try {
                            bos.write(bytes);
                        } catch (IOException e) {
                            log.error("read request body error...", e);
                        }
                    });

            return bos.toByteArray();
        }
    }

    @SneakyThrows
    public static Date toDate(String date){
        SimpleDateFormat formatter;
        if(date.length()>5) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else{
            formatter = new SimpleDateFormat("HH:mm");
        }
        return formatter.parse(date);
    }

    public static String toString(Date date){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  dateFormat.format(date);
    }

    public static LocalDateTime toLocalDtFromString(String date){
        var dt = toDate(date);
        return convertToLocalDateTimeViaInstant(dt);
    }

    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


}

