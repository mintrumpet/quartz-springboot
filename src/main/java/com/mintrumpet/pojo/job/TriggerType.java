package com.mintrumpet.pojo.job;

/**
 * @Author: david
 * @Description:
 * @Date； 2018/9/10
 **/
public enum TriggerType {
    SIMPLE(0),
    CRON(1);
    private Integer value;

    TriggerType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

}