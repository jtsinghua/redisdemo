package com.freetsinghua.redisdemo2.constant;

/**
 * @author z.tsinghua
 * @date 2018/10/30
 */
public enum Constants {
    /** Redis中的hash表 */
    MESSAGE_HASH_KEY("messages"),
    RETURN_OK("00000000"),
    RETURN_ERROR("90000000"),
    RETURN_ERROR_LOST_REQUIRED_ARGUMENT("10000000"),
    RETURN_ERROR_NOT_ONLINE("20000000");

    private String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
