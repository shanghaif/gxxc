package com.cwb.platform.job.config;

import com.cwb.platform.job.job.OrderFulfillJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class ScheduleComponent {

    Logger errorLog = LoggerFactory.getLogger("error_info");

    @Autowired
    private SchedulerFactoryBean schedulerFactory;

    public void startScheduler() {
        // gps同步job
        JobDetail jobDetail = JobBuilder.newJob(OrderFulfillJob.class).withIdentity(OrderFulfillJob.class.getName(), "GPSSync")
                .build();


        // gps同步定执行周期，每10s执行一次
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/10 * * * * ? ");


        // gps同步创建一个trigger
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(OrderFulfillJob.class.getName(), "GPSSync")
                .withSchedule(scheduleBuilder).build();


        try {
            schedulerFactory.getScheduler().scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            errorLog.error("任务创建失败", e);
        }
    }
}
