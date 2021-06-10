package com.mintrumpet.service;

import com.mintrumpet.pojo.job.QuartzJob;
import com.mintrumpet.quartz.factory.TriggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: david
 * @Description: quartz服务，创建任务、移除任务
 * @Date； 2019/5/5
 **/
@Service
public class QuartzService {

    @Autowired
    private TriggerFactory triggerFactory;

    /**
     * 创建触发器（定时任务）
     *
     * @param job
     */
    public void createTrigger(QuartzJob job) {
        triggerFactory.createTrigger(job);
    }

    /**
     * 移除job任务
     *
     * @param jobName
     * @param triggerName
     */
    public void removeJob(String jobName, String triggerName) {
        triggerFactory.removeJob(jobName, triggerName);
    }
}
