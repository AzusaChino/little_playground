package cn.az.code.config;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.support.CronTrigger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cn.az.code.mapper.CronMapper;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;

/**
 * @author ycpang
 * @since 2021-01-26 14:04
 */
@Configuration
@ConditionalOnCloudPlatform(CloudPlatform.HEROKU)
public class ScheduleConfig implements SchedulingConfigurer {

    @Resource
    private CronMapper cronMapper;

    /**
     * 注入scheduler到spring
     *
     * @param quartzJobFactory jf
     * @return Scheduler
     * @throws Exception e
     */
    @Bean
    public Scheduler scheduler(QuartzJobFactory quartzJobFactory) throws Exception {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(quartzJobFactory);
        factoryBean.afterPropertiesSet();
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * 基于数据表动态处理定时任务
     *
     * @param scheduledTaskRegistrar str
     */
    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(
                () -> System.out.println("Hello" + LocalDateTime.now()),
                triggerContext -> {
                    String cron = cronMapper.getCron();
                    if (StrUtil.isBlank(cron)) {
                        cron = "* * * 1 * * *";
                    }
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                });
    }

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() << 1,
                new ThreadFactoryBuilder().setNameFormat("scheduler-").build());
    }
}
