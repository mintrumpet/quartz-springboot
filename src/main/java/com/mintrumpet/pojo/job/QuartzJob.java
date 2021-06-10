package com.mintrumpet.pojo.job;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: david
 * @Description:
 * @Date； 2018/11/7
 **/
@Data
public class QuartzJob implements Serializable {

    private static final long serialVersionUID = 6575630849578006711L;

    private String jobName;

    private TriggerType triggerType;

    /**
     * 重试次数
     */
    private Integer retryTime;

    private String cronExpression;

    private Long duration;

    /**
     * bean名
     */
    private String beanName;

    /**
     * 方法名
     */
    private String methodName;

    private Object[] argsList;

    private Class[] argsClassList;

    public QuartzJob(String jobName, TriggerType triggerType, String cronExpression, Long duration, String beanName, String methodName) {
        this.jobName = jobName;
        this.triggerType = triggerType;
        this.cronExpression = cronExpression;
        this.duration = duration;
        this.beanName = beanName;
        this.methodName = methodName;

        this.retryTime = 0;
    }
}
