package com.freetsinghua.redisdemo2.util;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;

/**
 * 记录信息到文件中，比如一共发送了多少条数据
 * @author z.tsinghua
 * @date 2018/11/1
 */
@Component
public class FileLogger {

    private static final File FILE = new File("E:/redis-logs/count.log");

    private BufferedWriter writer;

    /**
     * 记录信息到文件中
     *
     * @param message 信息
     */
    public void log(String message) {
        try {
            writer.append(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 初始化writer */
    @PostConstruct
    public void init() {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 关闭writer */
    @PreDestroy
    public void destroy() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
