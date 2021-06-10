package com.mintrumpet.service;

import com.mintrumpet.pojo.job.QuartzJob;
import com.mintrumpet.pojo.job.TriggerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *
 *
 * Created by david chow on 6/10/21.
 * </pre>
 *
 * @author david chow
 **/
@Slf4j
@Service
public class CustomService {

    @Autowired
    private QuartzService quartzService;

    /**
     * 创建job模拟
     *
     */
    public void createJob() {
        //创建job，调用方法
        String jobName = "customJob " + System.currentTimeMillis();
        QuartzJob job = new QuartzJob(jobName,
                TriggerType.SIMPLE, null, 10 * 1000L,
                "customService", "doJob");
        job.setArgsList(new String[]{"nbac9576201"});
        job.setArgsClassList(new Class[]{String.class});
        quartzService.createTrigger(job);
    }

    /**
     * 任务实际业务逻辑模拟
     *
     * @param jobNo
     */
    public void doJob(String jobNo) {
      log.info("do the job jobNo [{}]", jobNo);
    }
}
