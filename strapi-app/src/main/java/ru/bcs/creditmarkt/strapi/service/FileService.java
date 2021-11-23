package ru.bcs.creditmarkt.strapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface FileService {

    void loadAndReadXlsFileList(List<MultipartFile> multipartFileList);
}
