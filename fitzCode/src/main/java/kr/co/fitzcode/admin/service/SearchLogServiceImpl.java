package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.SearchLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchLogServiceImpl implements SearchLogService {

    private final SearchLogMapper searchLogMapper;

    @Override
    public void saveSearchLog(Integer userId, String keyword) {
        Long count = searchLogMapper.countValidKeyword(keyword);

        if (count != null && count > 0) {
            searchLogMapper.insertSearchLog(userId, keyword);
        } else {
            log.debug("키워드를 찾을 수 없음 PRODUCT or CATEGORY : keyword={}", keyword);
        }
    }
}