package com.tu.jiu.quan;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.tu.jiu.quan.api.RemoteSocketIoService;
import com.tu.jiu.quan.config.Constants;
import com.tu.jiu.quan.input.BaseMsg;
import com.tu.jiu.quan.input.WebClientInput;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author tujiuquan@163.com
 **/
@Component
@AutoConfigureAfter(RemoteSocketIoService.class)
public class ClientCommandLineRunner implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger("ClientCommandLineRunner");
    ThreadLocal<Map<String, HashSet<String>>> mapThreadLocal = new ThreadLocal<>();
    ThreadPoolExecutor threadPool = null;

    @Resource
    private RemoteSocketIoService remoteSocketIoService;

    @PostConstruct
    private void initThreadPool() {
        this.threadPool = new ThreadPoolExecutor(1, 10, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5), new ThreadPoolExecutor.DiscardPolicy());
    }

    @PreDestroy
    private void clearMap() {
        mapThreadLocal.remove();
    }

    @Override
    public void run(String... args) {
        // 服务端socket.io连接通信地址
        String url = "http://127.0.0.1:58080";
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            options.reconnectionAttempts = 10;
            // 失败重连的时间间隔
            options.reconnectionDelay = 1500;
            // 连接超时时间(ms)
            options.timeout = 1500;
            // userId: 唯一标识 传给服务端存储
            final Socket socket = IO.socket(url + "?userId=user_feeder_service", options);

            socket.on(Socket.EVENT_CONNECT, args1 -> socket.send("hello, I`m feeder service!"));

            // 自定义事件`connected` -> 接收服务端成功连接消息
            socket.on("connected", objects -> log.debug("服务端,connected:{}", objects[0].toString()));

            // 自定义事件`push_data_event` -> 接收服务端消息
            socket.on("push_data_event", objects -> {
                log.debug("服务端,push_data_event:{}", objects[0].toString());
                BaseMsg baseMsg = new Gson().fromJson(objects[0].toString(), BaseMsg.class);
                if (Objects.nonNull(baseMsg)) {
                    WebClientInput webClientInput = baseMsg.getWebClientInput();
                    if (Objects.nonNull(webClientInput)) {
                        String bizModule = webClientInput.getBizModule();
                        String dataType = webClientInput.getDataType();
                        HashSet<String> userSet = mapThreadLocal.get().getOrDefault(String.join("@", bizModule, dataType), new HashSet<>());
                        userSet.add(webClientInput.getToken());
                        mapThreadLocal.get().put(String.join("@", bizModule, dataType), userSet);
                        log.debug("服务端,BizModule:{}", webClientInput.getBizModule());
                    }
                }
            });

            threadPool.execute(() -> {
                while (true) {
                    try {
                        if (CollUtil.isNotEmpty(mapThreadLocal.get())) {
                            (mapThreadLocal.get()).forEach((k, v) -> {
                                String[] strings = k.split("@");
                                if (CollUtil.isNotEmpty(v)) {
                                    v.forEach(userId -> remoteSocketIoService.pushMessageToUser(userId, "假装这是一条正经的消息，然后你自己解析一下！"));
                                }
                            });
                        }
                        Thread.sleep(1000 * 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            // 自定义事件`myBroadcast` -> 接收服务端广播消息
            // socket.on("myBroadcast", objects -> log.debug("服务端,myBroadcast：{}", objects[0].toString()));

            socket.connect();
            threadPool.execute(() -> {
                // todo 后续将考虑通过定时通讯取拉取保持通讯的用户信息，加速消息推送的效率和准确性
                while (true) {
                    try {
                        Thread.sleep(3000);
                        // 自定义事件`push_data_event` -> 向服务端发送消息
                        BaseMsg baseMsg = new BaseMsg();
                        baseMsg.setClientType(Constants.SERVICE_CLIENT);
                        socket.emit("push_data_event", JSONUtil.toJsonStr(baseMsg));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
