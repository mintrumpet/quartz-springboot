package com.mintrumpet.quartz.factory;

import com.mintrumpet.pojo.job.QuartzJob;
import com.mintrumpet.pojo.job.TriggerType;
import com.mintrumpet.quartz.job.CustomJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @Author: david
 * @Description:
 * @Date； 2018/9/10
 **/
@Component
@Slf4j
public class TriggerFactory {

    @Autowired
    private Scheduler scheduler;

    public Trigger createTrigger(QuartzJob job) {
        try {
            String name = job.getJobName();
            if (Objects.isNull(name)) return null;
            log.info("创建quartz trigger，名称 {}", name);

            TriggerKey triggerKey = new TriggerKey(name, job.getClass().getName());
            Trigger tri = scheduler.getTrigger(triggerKey);
            if (Objects.nonNull(tri)) {
                return tri;
            } else {
                TriggerType type = job.getTriggerType();
                switch (type) {
                    case SIMPLE: {
                        Long duration = job.getDuration();
                        String groupName = job.getClass().getName();
                        Long startAtTime;
                        if (Objects.nonNull(duration)) {
                            startAtTime = System.currentTimeMillis() + duration;
                        } else {
                            startAtTime = System.currentTimeMillis();
                        }
                        JobDetail jobDetail = JobBuilder.newJob(CustomJob.class).withIdentity(name, groupName)
                                .build();
                        jobDetail.getJobDataMap().put("quartzJob", job);
                        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, groupName)
                                .startAt(new Date(startAtTime)).build();
                        scheduler.scheduleJob(jobDetail, trigger);

                        return trigger;
                    }
                    case CRON: {
                        String cronExpression = job.getCronExpression();
                        if (Objects.nonNull(cronExpression)) {
                            String groupName = job.getClass().getName();
                            JobDetail jobDetail = JobBuilder.newJob(CustomJob.class).withIdentity(name, groupName)
                                    .build();
                            jobDetail.getJobDataMap().put("quartzJob", job);
                            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                                    .cronSchedule(cronExpression)
                                    .withMisfireHandlingInstructionDoNothing();
                            Trigger scheduleTrigger = TriggerBuilder.newTrigger().withIdentity(name, groupName)
                                    .withSchedule(scheduleBuilder).build();
                            scheduler.scheduleJob(jobDetail, scheduleTrigger);

                            return scheduleTrigger;
                        } else {
                            return null;
                        }
                    }
                    default: {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("createTrigger error: ", e);
            return null;
        }

    }

    /**
     * 重试job
     *
     * @param job
     */
    public void reTrySimpleJob(QuartzJob job) {
        try {
            job.setJobName(job.getJobName() + "10");
            String name = job.getJobName();

            log.info("重试任务，名称 {}", name);

            TriggerKey triggerKey = new TriggerKey(name, job.getClass().getName());
            Trigger tri = scheduler.getTrigger(triggerKey);
            if (Objects.isNull(tri)) {
                TriggerType type = job.getTriggerType();
                if (type.equals(TriggerType.SIMPLE)) {
                    String groupName = job.getClass().getName();
                    //递增重试，每 5 * retryTime ^ 2
                    Long startAtTime = System.currentTimeMillis() + new Double(5000 * Math.pow(job.getRetryTime(), 2)).longValue();
                    JobDetail jobDetail = JobBuilder.newJob(CustomJob.class).withIdentity(name, groupName)
                            .build();
                    jobDetail.getJobDataMap().put("quartzJob", job);
                    Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, groupName)
                            .startAt(new Date(startAtTime)).build();
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("retry error: ", e);
        }
    }

    /**
     * 移除任务
     *
     * @param jobName
     * @param triggerName
     */
    public void removeJob(String jobName, String triggerName) {
        try {
            String triggerGroupName = QuartzJob.class.getName();
            String jobGroupName = QuartzJob.class.getName();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            //停止触发器
            scheduler.pauseTrigger(triggerKey);
            //移除触发器
            scheduler.unscheduleJob(triggerKey);
            //删除任务
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (Exception e) {
            log.error("removeJob error: ", e);
            log.error("移除 job[{}] 发生异常", jobName);
        }
    }
}
