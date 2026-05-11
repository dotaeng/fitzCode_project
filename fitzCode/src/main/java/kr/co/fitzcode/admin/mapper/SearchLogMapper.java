package kr.co.fitzcode.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SearchLogMapper {

    // 특정 검색어의 유효한 검색 횟수를 조회
    Long countValidKeyword(@Param("keyword") String keyword);

    // 검색 기록을 DB에 추가
    void insertSearchLog(@Param("userId") Integer userId, @Param("keyword") String keyword);
}