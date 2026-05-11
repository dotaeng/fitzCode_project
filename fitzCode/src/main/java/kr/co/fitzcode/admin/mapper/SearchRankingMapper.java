package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.SearchRankingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchRankingMapper {
    // 상위 검색 키워드 조회
    List<SearchRankingDTO> selectTopSearchKeywords();

    // 검색 순위 인서트 또는 업데이트
    void insertOrUpdateSearchRanking(@Param("keyword") String keyword, @Param("ranking") int ranking, @Param("date") java.time.LocalDate date);
}