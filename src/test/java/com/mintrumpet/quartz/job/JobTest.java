package com.mintrumpet.quartz.job;

import com.mintrumpet.App;
import com.mintrumpet.pojo.job.QuartzJob;
import com.mintrumpet.pojo.job.TriggerType;
import com.mintrumpet.service.CustomService;
import com.mintrumpet.service.QuartzService;
import com.mintrumpet.util.BeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;

import static org.junit.Assert.*;

/**
 * <pre>
 *
 *
 * Created by david chow on 6/10/21.
 * </pre>
 *
 * @author david chow
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class JobTest {

    @Autowired
    private CustomService customService;

    @Autowired
    protected ApplicationContext applicationContext;

    @Test
    public void execute() {
        BeanManager.setApplicationContext(applicationContext);
        customService.createJob();

        //用sleep模拟定时
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}