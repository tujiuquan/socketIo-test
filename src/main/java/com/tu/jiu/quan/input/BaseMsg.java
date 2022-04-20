package com.tu.jiu.quan.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author tujiuquan
 */
@Data
@ApiModel(description = "应用基础数据结构")
public class BaseMsg {

    @ApiModelProperty("浏览器客户端类别：webClient；后台业务系统客户端：serviceClient")
    private String clientType;

    @ApiModelProperty("web端发送的消息")
    private WebClientInput webClientInput;

    @ApiModelProperty("业务系统发送的消息")
    private ServiceClientInput serviceClientInput;


}
