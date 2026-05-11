package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.common.dto.ErrorLogDTO;
import kr.co.fitzcode.common.dto.VisitorDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface LogService {

    List<ErrorLogDTO> getFilteredErrorLogs(LocalDateTime startDate, LocalDateTime endDate, int minLevel);

    List<VisitorDTO> getFilteredVisitorLogs(LocalDateTime startDate, LocalDateTime endDate);

    void saveErrorLog(ErrorLogDTO errorLogDTO);
}