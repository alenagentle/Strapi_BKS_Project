package ru.bcs.creditmarkt.strapi.client.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/files")
public interface FileServiceApi {

    @PostMapping("/upload")
    void uploadFile(@RequestParam("files") List<MultipartFile> multipartFileList);

}
