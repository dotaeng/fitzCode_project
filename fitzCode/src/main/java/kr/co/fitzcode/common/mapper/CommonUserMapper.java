package kr.co.fitzcode.common.mapper;

import kr.co.fitzcode.common.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonUserMapper {

    UserDTO getUserByEmail(String email);

    String getUserEmailByUserId(int id);
}