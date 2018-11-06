package com.freetsinghua.redisdemo2.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.freetsinghua.redisdemo2.constant.Constants;
import com.freetsinghua.redisdemo2.model.Message;
import com.freetsinghua.redisdemo2.service.MessageService;
import com.freetsinghua.redisdemo2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 用HTTP发送消息 注意，因为HTTP和websocket之间需要升级协议，对性能有影响，最好还是用websocket发送消息
 *
 * @author z.tsinghua
 * @date 2018/10/29
 */
@Controller
public class MessageController {
    private final MessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 发送消息方法
     *
     * @param data json字符串
     * @return 返回处理结果
     */
    @PostMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(@RequestBody String data) {
        Map<String, Object> result = new HashMap<>(0);
        JSONObject obj = JSONObject.parseObject(data);
        boolean isAllValid = checkRequested(obj);

        if (!isAllValid) {
            result.put("status", Constants.RETURN_ERROR_LOST_REQUIRED_ARGUMENT.getValue());
            result.put("msg", "必要参数缺失");

            return JSON.toJSONString(result);
        }

        Message message = StringUtils.stringToMessage(data);

        try {
            String status = messageService.sendMessage(message);
            result.put("status", status);
        } catch (Exception e) {
            logger.error("出现错误：【{}】", e.getMessage(), e);
            result.put("status", Constants.RETURN_ERROR.getValue());
            result.put("msg", e.getMessage());
        }

        return JSON.toJSONString(result);
    }

    /**
     * 检测是否已经包含必要的字段
     *
     * @param data 这是一个JSONObject实例
     * @return 如果所有必需字段已经齐全，则返回true，否则返回false
     */
    private boolean checkRequested(JSONObject data) {

        if (StringUtils.isEmpty(data.getString("src"))) {
            logger.error("参数src不能为空字符串或null");
            return false;
        }

        if (StringUtils.isEmpty(data.getString("dest"))) {
            logger.error("参数dest不能为字符串或null");
            return false;
        }

        if (StringUtils.isEmpty(data.getString("msg"))) {
            logger.error("参数msg不能为null");
        }

        return true;
    }
}
