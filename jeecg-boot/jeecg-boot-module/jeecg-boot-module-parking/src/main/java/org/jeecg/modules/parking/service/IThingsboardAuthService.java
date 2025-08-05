package org.jeecg.modules.parking.service;

public interface IThingsboardAuthService {

    /**
     * 获取有效的访问令牌
     * @return Access Token
     */
    String getAccessToken();
} 