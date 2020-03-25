package com.lsd.fun.modules.cos.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.modules.cos.config.QiNiuProperties;
import com.lsd.fun.modules.cos.service.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import lombok.extern.slf4j.Slf4j;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.ShiroUtils;
import com.lsd.fun.common.utils.SpringContextUtils;
import com.lsd.fun.modules.cos.dao.TFileDao;
import com.lsd.fun.modules.cos.entity.TFileEntity;
import com.lsd.fun.modules.cos.service.TFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by lsd
 * 2019-12-03 10:14
 */
@Slf4j
@Service
public class TFileServiceImpl extends ServiceImpl<TFileDao, TFileEntity> implements TFileService {

    @Value("#{funConfig.qiniu}")
    private QiNiuProperties qiNiuProperties;

    // Map<fileType,relativePath>
    public static Map<Integer, String> relativePathMap;

    @PostConstruct
    public void initRelativePathMap() {
        relativePathMap = Map.of(
                0, qiNiuProperties.getOtherPath(),
                1, qiNiuProperties.getAvatarPath(),
                2, qiNiuProperties.getImagePath(),
                3, qiNiuProperties.getVideoPath()
        );
    }

    @Transactional
    @Override
    public TFileEntity upload(MultipartFile file, Integer fileType, LocalDateTime expiredAt) {
        QiNiuService qiNiuService = getQiniuCOSService();

        final String originalFilename = file.getOriginalFilename();
        final String extensionName = "." + StringUtils.substringAfterLast(originalFilename, ".");
        final String relativePath = relativePathMap.get(fileType) == null ? qiNiuProperties.getOtherPath() : relativePathMap.get(fileType);
        // 云端路径
        String cosFilePath = relativePath + genePath(null, extensionName);
        // 上传云端
        Response response;
        try {
            response = qiNiuService.uploadFile(file.getInputStream(), cosFilePath);
        } catch (IOException e) {
            log.error("文件读入失败", e);
            throw new RRException("文件上传失败");
        }
        // 获取上传用户信息
        Long uploaderId = null;
        try {
            uploaderId = ShiroUtils.getUserId();
        } catch (NullPointerException ignored) {
        }
        // 入文件表
        final var tFileEntity = new TFileEntity()
                .setOriginalFilename(originalFilename)
                .setUploaderId(uploaderId)
                .setMimeType(file.getContentType())
                .setPath(cosFilePath)
                .setSize(file.getSize());
        try {
            this.save(tFileEntity);
        } catch (Exception e) {
            // 删除云对象存储
            this.delete(cosFilePath);
            log.error("上传文件入库失败", e);
            throw new RRException("上传失败");
        }
        return tFileEntity;
    }

    @Transactional
    @Override
    public void deleteById(Long tFileId) {
        Optional.ofNullable(
                this.lambdaQuery().select(TFileEntity::getPath).eq(TFileEntity::getId, tFileId).one()
        ).ifPresent(tFile -> this.delete(tFile.getPath()));
    }

    @Transactional
    @Override
    public void delete(String cosPath) {
        QiNiuService qCloudCOSService = getQiniuCOSService();
        try {
            qCloudCOSService.delete(cosPath);
            // 更新数据库
            this.lambdaUpdate().eq(TFileEntity::getPath, cosPath).set(TFileEntity::getDeletedAt, LocalDateTime.now()).update();
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }


    /**
     * 若配置了 fun.qiniu.enable = true 才返回 QiNiuService 的 Bean 对象
     */
    private QiNiuService getQiniuCOSService() {
        return Optional.ofNullable(SpringContextUtils.getBean(QiNiuService.class))
                .orElseThrow(() -> new RRException("COS服务未启用"));
    }

}
