package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    //All the files related operations can be placed here in this class
    //This way, we can keep remaining code clean and achieve a centralized way for handling file related operations

    @Override
    public String uploadImage(String path, MultipartFile imageFile) throws IOException {

        //Get the file name of original file
        String originalFileName = imageFile.getOriginalFilename(); //.getOriginalFileName() will give us entire name
        //with its extension
        //If we use .getName() here, we will get StringIndexOutOfBoundException

        //Rename the file uniquely i.e. Generate a Unique file name
        String randomId = UUID.randomUUID().toString(); //UUID is an in-build class
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        //e.g.: hills.jpg (originalFileName) ===> 4545 (randomId) ===> 4545.jpg (newFileName)
        String filePath = path + File.separator + fileName;

        //Check if path exists and create
        File folder = new File(path); //creating a File object from the received path
        if (!folder.exists())
            folder.mkdir();

        //Upload to the server
        Files.copy(imageFile.getInputStream(), Paths.get(filePath));

        //return the file name
        return fileName;
    }
}
