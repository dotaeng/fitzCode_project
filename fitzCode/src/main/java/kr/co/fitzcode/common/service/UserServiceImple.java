package kr.co.fitzcode.common.service;

import kr.co.fitzcode.common.dto.UserDTO;
import kr.co.fitzcode.common.enums.UserRole;
import kr.co.fitzcode.common.mapper.CommonUserMapper;
import kr.co.fitzcode.common.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImple implements UserService {
    private final CommonUserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public UserDTO getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    public List<Integer> getUserRolesByUserId(int userId) {
        return userRoleMapper.findRolesByUserId(userId);
    }

    @Override
    public List<UserRole> findRolesInStringByUserId(int userId) {
        return userRoleMapper.findRolesInStringByUserId(userId);
    }

    @Override
    public String getUserEmailByUserId(int userId) {
        return userMapper.getUserEmailByUserId(userId);
    }

}