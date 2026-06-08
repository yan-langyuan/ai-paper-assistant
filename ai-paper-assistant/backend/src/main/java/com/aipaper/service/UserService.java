package com.aipaper.service;

import com.aipaper.dto.AuthResponse;
import com.aipaper.dto.LoginRequest;
import com.aipaper.dto.RegisterRequest;

public interface UserService {

    /**
     * 用户注册
     * @param req 注册请求（用户名、密码、邮箱、年级、专业）
     * @return 包含JWT令牌和用户名的认证响应
     */
    AuthResponse register(RegisterRequest req);

    /**
     * 用户登录
     * @param req 登录请求（用户名、密码）
     * @return 包含JWT令牌和用户名的认证响应
     */
    AuthResponse login(LoginRequest req);
}
