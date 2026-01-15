package com.rakesh.Contact.Manager.Services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public String uploadImage(MultipartFile contactImage);
    public String getUrlFromPublicId(String publicId);
}
