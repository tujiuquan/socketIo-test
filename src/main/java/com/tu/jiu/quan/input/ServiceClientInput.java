package com.tu.jiu.quan.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tujiuquan
 */
@Data
@ApiModel(description = "业务系统发送给WebSocket的消息，告知自己的所处理业务模块")
public class ServiceClientInput {

    @ApiModelProperty(value = "令牌")
    private String token;

    @ApiModelProperty("业务模块")
    private String bizModule;

    @ApiModelProperty("推送的数据类别")
    private String dataType;

    @ApiModelProperty("推送的业务数据")
    private String msgJsonString;

}
