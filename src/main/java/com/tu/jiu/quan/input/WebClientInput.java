package com.tu.jiu.quan.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tujiuquan
 */
@Data
@ApiModel(description = "WebClient发送的消息,告知需要推送的业务模块数据")
public class WebClientInput {

    @ApiModelProperty(value = "令牌")
    private String token;

    @ApiModelProperty("需要推送的数据类别")
    private String dataType;

    @ApiModelProperty("业务模块")
    private String bizModule;

}



