package com.tu.jiu.quan.api;


import com.tu.jiu.quan.api.factory.RemoteSocketIoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p> socket.io服务类 </p>
 *
 * @author : tujiuquan
 */
@FeignClient(contextId = "remoteSocketIoService", path = "/api/socketIo", value = "iot-feed-websocket", fallbackFactory = RemoteSocketIoFallbackFactory.class)
public interface RemoteSocketIoService {

    /**
     * 推送信息给指定客户端
     *
     * @param userId:     客户端唯一标识
     * @param msgContent: 消息内容
     */
    @PostMapping("pushMessageToUser")
    void pushMessageToUser(String userId, String msgContent);
}
