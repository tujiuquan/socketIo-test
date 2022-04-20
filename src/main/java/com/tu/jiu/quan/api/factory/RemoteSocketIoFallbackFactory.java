package com.tu.jiu.quan.api.factory;

import com.tu.jiu.quan.api.RemoteSocketIoService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 客户服务降级处理
 *
 * @author tujiuquan
 */

@Component
public class RemoteSocketIoFallbackFactory implements FallbackFactory<RemoteSocketIoService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteSocketIoFallbackFactory.class);

    @Override
    public RemoteSocketIoService create(Throwable throwable) {
        log.error("文件服务调用失败:{}", throwable.getMessage());
        return new RemoteSocketIoService() {
            /**
             * 推送信息给指定客户端
             *
             * @param userId     :     客户端唯一标识
             * @param msgContent : 消息内容
             */

            @Override
            public void pushMessageToUser(String userId, String msgContent) {
            }
        };
    }
}
