package com.stonedt.intelligence.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * description: ExportUtil <br>
 * date: 2020/4/17 19:44 <br>
 * author: 导出工具类 <br>
 * version: 1.0 <br>
 */
@Component
public class ExportUtil {
    /**
     * @param header    标题（用逗号分隔英文逗号）
     * @param fileName  生成的文件名字
     * @param sheetName sheet名字
     * @param dataArray 需要生成的数据
     * @return void
     * @description: 生成表格工具类 <br>
     * @version: 1.0 <br>
     * @date: 2020/4/17 19:46 <br>
     * @author: huajiancheng <br>
     */

    public void createExcel(String header, String fileName, String sheetName, JSONArray dataArray, HttpServletResponse response) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            HSSFSheet sheet = workbook.createSheet(sheetName);
            String[] headers = header.split(",");
            HSSFRow row = sheet.createRow(0);  //创建表格的第一行
            for (int i = 0; i < headers.length; i++) {  //在excel表中添加表头
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }

            Integer rowNum = 1;//新增数据行，并且设置单元格数据
            for (int i = 0; i < dataArray.size(); i++) {
                try {
                    JSONObject dataJson = (JSONObject) dataArray.get(i);
                    JSONObject _sourceJson = dataJson.getJSONObject("_source");
                    HSSFRow row1 = sheet.createRow(rowNum);

                    String title = _sourceJson.getString("title");
                    String source_url = _sourceJson.getString("source_url");
                    String sourcewebsitename = _sourceJson.getString("sourcewebsitename");
                    String publish_time = _sourceJson.getString("publish_time");
                    String dataSourceClassification = _sourceJson.getString("dataSourceClassification");
                    Integer emotionalIndex = _sourceJson.getInteger("emotionalIndex");
                    String content = _sourceJson.getString("content");
                    String ner = _sourceJson.getString("ner");
                    JSONObject orgobject = JSONObject.parseObject(ner).getJSONObject("org");
                    String orgstr = "";
                    if(orgobject!=null) {
                    	for(String org:orgobject.keySet()){
                    		orgstr+=org+",";
                     }
                    }
                    if(orgstr.endsWith(",")) {
                    	orgstr = orgstr.substring(0, orgstr.length()-1);
                    }

                    String emotion = "";
                    if (emotionalIndex == 3) {
                        emotion = "敏感";
                    } else {
                        emotion = "非敏感";
                    }


                    if (StringUtils.isNotEmpty(content)) {
                        content = content.trim();
                        content = content.replaceAll(" ", "");
                        if (content.length() > 32767) {
                            content = content.substring(0, 32767);
                        }
                    }

                    row1.createCell(0).setCellValue((i + 1)); // 序号
                    row1.createCell(1).setCellValue(title); // 标题
                    row1.createCell(2).setCellValue(source_url); // url地址
                    row1.createCell(3).setCellValue(sourcewebsitename); // 媒体名称
                    row1.createCell(4).setCellValue(publish_time); // 发布日期
                    row1.createCell(5).setCellValue(dataSourceClassification); // 媒体类型
                    row1.createCell(6).setCellValue(emotion); // 属性（敏感，非敏感）
                    row1.createCell(7).setCellValue(content); // 摘要
                    row1.createCell(8).setCellValue(orgstr); // 实体
                    rowNum++;
                } catch (Exception e) {
                    continue;
                }
            }

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=utf-8''" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
