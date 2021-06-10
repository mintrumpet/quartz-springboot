package com.mintrumpet.quartz.job;

import com.mintrumpet.pojo.job.QuartzJob;
import com.mintrumpet.pojo.job.TriggerType;
import com.mintrumpet.quartz.factory.TriggerFactory;
import com.mintrumpet.util.BeanManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @Author: david
 * @Description:
 * @Date； 2018/11/7
 **/
// 控制quartz不会同时执行多个实例，基于jobDetail
//@DisallowConcurrentExecution
// 执行execute后更新jobDataMap，基于jobDetail
//@PersistJobDataAfterExecution
@Component
public class CustomJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(CustomJob.class);

    @Autowired
    private TriggerFactory triggerFactory;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        QuartzJob quartzJob = (QuartzJob) jobExecutionContext.getMergedJobDataMap().get("quartzJob");
        String jobName = quartzJob.getJobName();

        //triggerType
        TriggerType triggerType = quartzJob.getTriggerType();

        try {
            /* 反射调用方法 */
            String beanName = quartzJob.getBeanName();
            String methodName = quartzJob.getMethodName();
            Object[] args = quartzJob.getArgsList();
            Class[] argsClasses = quartzJob.getArgsClassList();

            Object bean = BeanManager.getBean(beanName);
            Method method = ReflectionUtils.findMethod(bean.getClass(), methodName, argsClasses);
            ReflectionUtils.invokeMethod(method, bean, args);

            logger.info("[{}] 调用完成", jobName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("exception：", e);
            logger.info("[{}] 调用失败", jobName);

            //根据任务定义，只对普通任务实施重试
            //重试次数为最大为三次
            if (triggerType.equals(TriggerType.SIMPLE) && quartzJob.getRetryTime() < 3) {
                //重试次数递增
                quartzJob.setRetryTime(quartzJob.getRetryTime() + 1);
                triggerFactory.reTrySimpleJob(quartzJob);
            }
        }
    }

}
