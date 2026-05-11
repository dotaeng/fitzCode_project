package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.LogMapper;
import kr.co.fitzcode.common.dto.ErrorLogDTO;
import kr.co.fitzcode.common.dto.VisitorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogMapper logMapper;

    @Override
    public List<ErrorLogDTO> getFilteredErrorLogs(LocalDateTime startDate, LocalDateTime endDate, int minLevel) {
        return logMapper.findFilteredErrorLogs(startDate, endDate, minLevel);
    }

    @Override
    public List<VisitorDTO> getFilteredVisitorLogs(LocalDateTime startDate, LocalDateTime endDate) {
        return logMapper.findFilteredVisitorLogs(startDate, endDate);
    }

    @Override
    public void saveErrorLog(ErrorLogDTO errorLogDTO) {
        logMapper.insertErrorLog(errorLogDTO);
    }
}