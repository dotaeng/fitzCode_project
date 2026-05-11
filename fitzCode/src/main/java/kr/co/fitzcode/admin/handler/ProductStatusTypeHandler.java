package kr.co.fitzcode.admin.handler;

import kr.co.fitzcode.common.enums.ProductStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

// ProductStatus enum 타입을 데이터베이스의 INTEGER 타입과 매핑하기 위한 커스텀 TypeHandler
// ProductStatus enum 값을 데이터베이스에 정수로 저장하고, 조회 시 정수에서 enum 변환
public class ProductStatusTypeHandler extends BaseTypeHandler<ProductStatus> {

    // PreparedStatement에 ProductStatus enum 값을 설정할 때 호출
    // 비어 있지 않은(non-null) ProductStatus 값을 데이터베이스에 저장하기 위해 정수 코드로 변환
    // ps: PreparedStatement 객체, i: 파라미터 인덱스, parameter: 설정할 ProductStatus 값, jdbcType: JDBC 타입
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode()); // ProductStatus enum 코드 값을 정수로 설정
    }

    // ResultSet에서 열 이름(columnName)을 통해 ProductStatus 값을 가져올 때 호출
    // 데이터베이스에서 조회한 정수 코드를 ProductStatus enum 변환
    // rs: ResultSet 객체, columnName: 열 이름, 반환값: 변환된 ProductStatus enum
    @Override
    public ProductStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return ProductStatus.fromCode(code); // 정수 코드를 ProductStatus enum 반환
    }

    // ResultSet에서 열 인덱스(columnIndex)를 통해 ProductStatus 값을 가져올 때 호출
    // 데이터베이스에서 조회한 정수 코드를 ProductStatus enum 변환
    // rs: ResultSet 객체, columnIndex: 열 인덱스, 반환값: 변환된 ProductStatus enum
    @Override
    public ProductStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return ProductStatus.fromCode(code); // 정수 코드를 ProductStatus enum 반환
    }

    // CallableStatement에서 열 인덱스(columnIndex)를 통해 ProductStatus 값을 가져올 때 호출
    // 주로 저장 프로시저 호출 후 결과를 가져올 때 사용
    // cs: CallableStatement 객체, columnIndex: 열 인덱스, 반환값: 변환된 ProductStatus enum
    @Override
    public ProductStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return ProductStatus.fromCode(code); // 정수 코드를 roductStatus enum 반환
    }
}