package com.example.library.service.Impl;

import com.example.library.model.Image;
import com.example.library.service.ImageService;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public List<Image> findProductImages() {
        return null;
    }

    @Override
    public List<Image> findAll() {
        return null;
    }


}
