package kr.co.fitzcode.admin.handler;

import kr.co.fitzcode.common.enums.DeliveryStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@MappedTypes(DeliveryStatus.class)
public class DeliveryTypeHandler extends BaseTypeHandler<DeliveryStatus> {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DeliveryStatus parameter, JdbcType jdbcType) throws SQLException {
        logger.debug("Setting parameter: {}", parameter.getCode());
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public DeliveryStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        logger.debug("Retrieved code from column '{}': {}", columnName, code);
        if (rs.wasNull()) {
            logger.debug("Column '{}' is NULL", columnName);
            return null;
        }
        try {
            return DeliveryStatus.fromCode(code);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid delivery status code: {}. Available codes: {}", code, Arrays.toString(DeliveryStatus.values()), e);
            throw e;
        }
    }

    @Override
    public DeliveryStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        logger.debug("Retrieved code from index '{}': {}", columnIndex, code);
        if (rs.wasNull()) {
            logger.debug("Column at index '{}' is NULL", columnIndex);
            return null;
        }
        try {
            return DeliveryStatus.fromCode(code);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid delivery status code: {}. Available codes: {}", code, Arrays.toString(DeliveryStatus.values()), e);
            throw e;
        }
    }

    @Override
    public DeliveryStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        logger.debug("Retrieved code from callable statement index '{}': {}", columnIndex, code);
        if (cs.wasNull()) {
            logger.debug("Column at index '{}' is NULL", columnIndex);
            return null;
        }
        try {
            return DeliveryStatus.fromCode(code);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid delivery status code: {}. Available codes: {}", code, Arrays.toString(DeliveryStatus.values()), e);
            throw e;
        }
    }
}