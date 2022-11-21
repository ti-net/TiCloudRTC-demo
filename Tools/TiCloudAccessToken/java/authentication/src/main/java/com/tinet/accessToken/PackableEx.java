package com.tinet.accessToken;

/**
 * @Author: zhoujiang
 * @Date: 2022/7/25 16:30
 */
public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
