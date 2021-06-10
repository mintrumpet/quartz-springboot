package com.mintrumpet.quartz.factory;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

/**
 * @Author: david
 * @Description:
 * @Date； 2018/8/28
 **/
@Configuration
@EnableScheduling
public class QuartzConfiguration {

    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

        /* 不被序列化 */
        private transient AutowireCapableBeanFactory beanFactory;

        /**
         * 设置上下文
         *
         * @param applicationContext
         * @throws BeansException
         */
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            beanFactory = applicationContext.getAutowireCapableBeanFactory();
        }

        /**
         * job 实例化 交由 spring ioc管理
         *
         * @param bundle
         * @return
         */
        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);

            /**
             * job实例化交由spring ioc
             */
            beanFactory.autowireBean(job);
            return job;
        }
    }

    /**
     * 配置任务工厂实例
     *
     * @param applicationContext
     * @return
     */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * 工厂bean
     *
     * @param jobFactory
     * @param dataSource
     * @return
     */
    @Bean(destroyMethod = "destroy", autowire = Autowire.NO)
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // 交由调度器维护
        schedulerFactoryBean.setJobFactory(jobFactory);
        // 覆盖已存在任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        // 等待2秒开始执行调度器初始化
        schedulerFactoryBean.setStartupDelay(2);

        // 自动启动
        schedulerFactoryBean.setAutoStartup(true);
        // 设置数据源
        schedulerFactoryBean.setDataSource(dataSource);
        // 设置上下文
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        // 设置配置文件位置
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/quartz.properties"));

        return schedulerFactoryBean;
    }

}
