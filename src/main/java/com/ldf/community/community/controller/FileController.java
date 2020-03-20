package com.ldf.community.community.controller;

import com.ldf.community.community.dto.FileDTO;
import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.exception.CustomizeException;
import com.ldf.community.community.provider.QCloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 文件相关功能
 */
@Controller
public class FileController {

    @Autowired
    private QCloudProvider qCloudProvider;

    // 图片上传
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        String url = qCloudProvider.upload(file);
        if (url == null || url.length() <= 0) {
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setMessage("上传成功！");
        fileDTO.setUrl(url);
        return fileDTO;
    }
}
