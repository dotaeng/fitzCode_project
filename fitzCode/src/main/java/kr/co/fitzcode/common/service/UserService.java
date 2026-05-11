package kr.co.fitzcode.common.service;

import kr.co.fitzcode.common.dto.UserDTO;
import kr.co.fitzcode.common.enums.UserRole;

import java.util.List;

public interface UserService {
    UserDTO getUserByEmail(String email);

    List<Integer> getUserRolesByUserId(int userId);

    List<UserRole> findRolesInStringByUserId(int userId);

    String getUserEmailByUserId(int userId);
}