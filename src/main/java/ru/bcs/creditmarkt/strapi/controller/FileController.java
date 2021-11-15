package ru.bcs.creditmarkt.strapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.bcs.creditmarkt.strapi.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("files") List<MultipartFile> multipartFileList) {
        fileService.loadAndReadXlsFileList(multipartFileList);
    }
}
