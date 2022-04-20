package com.tu.jiu.quan.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p> 应用基础传入参数 </p>
 *
 * @author: zhengqing
 * @date: 2019/9/13 0013 1:57
 */
@Data
@ApiModel(description = "应用基础传入参数")
public class BaseInput {
    @ApiModelProperty(value = "令牌")
    private String token;
}
