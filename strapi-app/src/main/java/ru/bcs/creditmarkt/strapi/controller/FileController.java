package ru.bcs.creditmarkt.strapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.bcs.creditmarkt.strapi.client.api.FileServiceApi;
import ru.bcs.creditmarkt.strapi.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController implements FileServiceApi {

    private final FileService fileService;

    @Override
    public void uploadFile(@RequestParam("files") List<MultipartFile> multipartFileList) {
        fileService.treatXlcFileList(multipartFileList);
    }
}
