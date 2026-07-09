package com.capsule.utils;

import com.baidu.aip.contentcensor.AipContentCensor;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaiduCensorUtil implements InitializingBean { 

    @Value("${baidu.aip.appId}")
    private String appId;

    @Value("${baidu.aip.apiKey}")
    private String apiKey;

    @Value("${baidu.aip.secretKey}")
    private String secretKey;

    private AipContentCensor client;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化百度 AI 审核客户端
        this.client = new AipContentCensor(appId, apiKey, secretKey);
    }

    /**
     * 审核文本内容（胶囊正文、评论等）
     */
    public boolean checkText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return true;
        }
        try {
            JSONObject response = client.textCensorUserDefined(text);
            int conclusionType = response.optInt("conclusionType", 4);
            
            if (conclusionType == 2) {
                System.out.println("⚠️ 内容审核未通过！违规详情: " + response.toString());
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true; 
        }
    }
}