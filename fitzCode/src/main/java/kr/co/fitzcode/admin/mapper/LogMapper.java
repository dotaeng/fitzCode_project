package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.ErrorLogDTO;
import kr.co.fitzcode.common.dto.VisitorDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LogMapper {

    List<ErrorLogDTO> findFilteredErrorLogs(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minLevel") int minLevel);

    List<VisitorDTO> findFilteredVisitorLogs(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    void insertErrorLog(ErrorLogDTO errorLogDTO);
}