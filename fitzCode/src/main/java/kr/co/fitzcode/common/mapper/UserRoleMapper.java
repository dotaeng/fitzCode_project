package kr.co.fitzcode.common.mapper;

import kr.co.fitzcode.common.enums.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    List<Integer> findRolesByUserId(@Param("userId") int userId);

    List<UserRole> findRolesInStringByUserId(@Param("userId") int userId);
}