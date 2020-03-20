package com.ldf.community.community.provider;

import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.exception.CustomizeException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 腾讯云对象存储
 */
@Component
public class QCloudProvider {

    @Value("${qcloud.cos.secret-id}")
    private String secretId;
    @Value("${qcloud.cos.secret-key}")
    private String secretKey;
    @Value("${qcloud.cos.region}")
    private String regions;
    @Value("${qcloud.cos.bucketName}")
    private String bucketName;

    public String upload(MultipartFile file) throws IOException {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(regions);
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);

        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        String key = UUID.randomUUID().toString() + ".jpg";


        File localFile = File.createTempFile("temp", null);
        file.transferTo(localFile);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

        transferManager.shutdownNow();
        if (putObjectResult != null && !"".equals(putObjectResult.getETag().trim())) {
            // 设置1年后过期的签名，分发给客户端进行文件上传
            GeneratePresignedUrlRequest req =
                    new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
            Date expirationDate = new Date(System.currentTimeMillis() + 365L * 24L * 60L * 60L * 1000L);
            req.setExpiration(expirationDate);
            String url = cosClient.generatePresignedUrl(req).toString();
            cosClient.shutdown();
            return url;
        }
        throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
    }

}
