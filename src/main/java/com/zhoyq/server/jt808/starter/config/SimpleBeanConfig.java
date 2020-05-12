/*
 *  Copyright (c) 2020. 衷于栖 All rights reserved.
 *
 *  版权所有 衷于栖 并保留所有权利 2020。
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  作者：衷于栖（feedback@zhoyq.com）
 *  博客：https://www.zhoyq.com
 *  创建时间：2020
 *
 */

package com.zhoyq.server.jt808.starter.config;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.service.CacheService;
import com.zhoyq.server.jt808.starter.service.DataService;
import com.zhoyq.server.jt808.starter.service.impl.HashMapCacheService;
import com.zhoyq.server.jt808.starter.service.impl.SimpleDataServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/4
 */
@Slf4j
@Configuration
public class SimpleBeanConfig {

    @Autowired
    private ByteArrHelper byteArrHelper;

    @Autowired
    private Jt808Config jt808Config;

    @Bean
    @ConditionalOnMissingBean(DataService.class)
    public DataService dataService() {
        log.info("use default data service Bean.");
        return new SimpleDataServiceAdapter(byteArrHelper);
    }

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    public CacheService sessionService(){
        log.info("use default session service Bean.");
        return new HashMapCacheService();
    }
}
