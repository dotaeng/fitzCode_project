package kr.co.fitzcode.admin.handler;

import kr.co.fitzcode.common.enums.ProductSize;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

// ProductSize enum 타입을 데이터베이스의 INTEGER 타입과 매핑하기 위한 커스텀 TypeHandler
// @MappedTypes: 이 핸들러가 처리할 Java 타입(ProductSize)을 지정
// @MappedJdbcTypes: 이 핸들러가 매핑할 JDBC 타입(INTEGER)을 지정
@MappedTypes(ProductSize.class)
@MappedJdbcTypes(JdbcType.INTEGER)
public class ProductSizeTypeHandler extends BaseTypeHandler<ProductSize> {

    // PreparedStatement에 ProductSize enum 값을 설정할 때 호출
    // 비어 있지 않은(non-null) ProductSize 값을 데이터베이스에 저장하기 위해 정수 코드로 변환
    // ps: PreparedStatement 객체, i: 파라미터 인덱스, parameter: 설정할 ProductSize 값, jdbcType: JDBC 타입
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductSize parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode()); // Enum 코드 값을 정수로 설정
    }

    // ResultSet에서 열 이름(columnName)을 통해 ProductSize 값을 가져올 때 호출
    // 데이터베이스에서 조회한 정수 코드를 ProductSize enum으로 변환
    // rs: ResultSet 객체, columnName: 열 이름, 반환값: 변환된 ProductSize enum
    @Override
    public ProductSize getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return ProductSize.fromCode(code); // 정수 코드를 ProductSize enum 반환
    }

    // ResultSet에서 열 인덱스(columnIndex)를 통해 ProductSize 값을 가져올 때 호출
    // 데이터베이스에서 조회한 정수 코드를 ProductSize enum으로 변환
    // rs: ResultSet 객체, columnIndex: 열 인덱스, 반환값: 변환된 ProductSize enum
    @Override
    public ProductSize getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return ProductSize.fromCode(code); // 정수 코드를 ProductSize enum 반환
    }

    // CallableStatement에서 열 인덱스(columnIndex)를 통해 ProductSize 값을 가져올 때 호출
    // 주로 저장 프로시저 호출 후 결과를 가져올 때 사용
    // cs: CallableStatement 객체, columnIndex: 열 인덱스, 반환값: 변환된 ProductSize enum
    @Override
    public ProductSize getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return ProductSize.fromCode(code); // 정수 코드를 ProductSize enum 반환
    }
}