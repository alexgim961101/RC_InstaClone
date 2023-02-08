package com.rising.insta.utils.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileComponentImpl {
    String saveFile(MultipartFile file) throws IOException;
    byte[] downloadFile(String filename);
    String deleteFile(String filename);
    List<String> listAllFile();
}
