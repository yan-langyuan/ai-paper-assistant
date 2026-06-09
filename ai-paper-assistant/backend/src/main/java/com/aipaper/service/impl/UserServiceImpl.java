package com.aipaper.service.impl;

import com.aipaper.dto.AuthResponse;
import com.aipaper.dto.LoginRequest;
import com.aipaper.dto.RegisterRequest;
import com.aipaper.mapper.UserMapper;
import com.aipaper.model.User;
import com.aipaper.service.UserService;
import com.aipaper.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest req) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(req.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .grade(req.getGrade())
                .major(req.getMajor())
                .build();

        userMapper.insert(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        // 查找用户
        User user = userMapper.findByUsername(req.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }
}
