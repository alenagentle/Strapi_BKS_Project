package ru.bcs.creditmarkt.strapi.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface FileService {

    ResponseEntity<String> treatXlcFileList(List<MultipartFile> multipartFileList);
}
