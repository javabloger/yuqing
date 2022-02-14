
package com.stonedt.intelligence.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.stonedt.intelligence.dao.SystemDao;
import com.stonedt.intelligence.entity.WarningSetting;
import com.stonedt.intelligence.service.SystemService;
import com.stonedt.intelligence.util.ResultUtil;

/**
 * <p></p>
 * <p>Title: SystemServiceImpl</p>
 * <p>Description: </p>
 *
 * @author Mapeng
 * @date Apr 16, 2020
 */
@Service
@PropertySource(value = "file:./config/config.properties", encoding = "UTF-8")
public class SystemServiceImpl implements SystemService {

    @Autowired
    private SystemDao systemDao;

    @Value("${product.manual.path}")
    private String productManualPath;

    @Override
    public Map<String, Object> listWarning(Integer pageNum, Long userId, Long group_id) {
        Map<String, Object> result = new HashMap<String, Object>();
        PageHelper.startPage(pageNum, 10);
        List<WarningSetting> listWarning = systemDao.listWarning(userId, group_id);
        PageInfo<WarningSetting> pageInfo = new PageInfo<>(listWarning);
        result.put("list", listWarning);
        result.put("pageCount", pageInfo.getPages());
        result.put("dataCount", pageInfo.getTotal());
        return result;
    }

    @Override
    public boolean updateWarningStatusById(Integer warning_status, Long project_id) {
        boolean updateWarningStatusById = systemDao.updateWarningStatusById(warning_status, project_id);
        return updateWarningStatusById;
    }


    @Override
    public boolean getWarningWordById( Long project_id) {
        WarningSetting warningSetting = systemDao.getWarningWord( project_id);
        String warning_word = warningSetting.getWarning_word();
        if (warning_word == null || warning_word.equals("")) {
            return true;
        }
        return false;
    }

    @Override
    public WarningSetting getWarningByProjectId(Long project_id) {
        WarningSetting warningByProjectId = systemDao.getWarningByProjectId(project_id);
        return warningByProjectId;
    }

    @Override
    public ResultUtil updateWarning(WarningSetting warningSetting) {
        boolean updateWarning = systemDao.updateWarning(warningSetting);
        if (updateWarning) {
            return ResultUtil.build(200, "");
        }
        return ResultUtil.build(200, "");
    }

    /**
     * @param [map]
     * @return java.lang.Integer
     * @description: 添加预警 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/18 20:36 <br>
     * @author: huajiancheng <br>
     */
    @Override
    public Integer addWarning(Map<String, Object> map) {
        return systemDao.addWarning(map);
    }

    @Override
    public ResponseEntity<InputStreamResource> uploadProductManual() {
        ResponseEntity<InputStreamResource> response = null;
        try {
            File file = new File(productManualPath);
            if (file.isFile() && !file.exists()) {
                file.mkdirs();
            }
            String fileName = productManualPath.substring(productManualPath.lastIndexOf("/") + 1);
            InputStream inputStream = new FileInputStream(productManualPath);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            response = ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(inputStream));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return response;
    }

}
