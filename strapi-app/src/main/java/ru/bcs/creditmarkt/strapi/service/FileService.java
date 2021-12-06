package ru.bcs.creditmarkt.strapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface FileService {

    void treatXlcFileList(List<MultipartFile> multipartFileList);
}
