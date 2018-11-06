package com.freetsinghua.redisdemo2.model;

import java.util.Objects;

/**
 * 实现了序列化标记接口
 *
 * @author z.tsinghua
 * @date 2018/10/29
 */
public class Message {
    private String id;
    private String src;
    private String dest;
    private String msg;
    private String remark;

    public Message() {}

    public Message(String src, String dest, String msg) {
        this.src = src;
        this.dest = dest;
        this.msg = msg;
    }

    public Message(String id, String src, String dest, String msg, String remark) {
        this.id = id;
        this.src = src;
        this.dest = dest;
        this.msg = msg;
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public String getDest() {
        return dest;
    }

    public String getMsg() {
        return msg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(id, message.id)
                && Objects.equals(src, message.src)
                && Objects.equals(dest, message.dest)
                && Objects.equals(msg, message.msg)
                && Objects.equals(remark, message.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, src, dest, msg, remark);
    }

    @Override
    public String toString() {
        return "Message{"
                + "id='"
                + id
                + '\''
                + ", src='"
                + src
                + '\''
                + ", dest='"
                + dest
                + '\''
                + ", msg='"
                + msg
                + '\''
                + ", remark='"
                + remark
                + '\''
                + '}';
    }
}
