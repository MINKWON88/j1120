package ch09;

import java.sql.*;
import javax.sql.*;
import javax.naming.*;

public class MemberDao { // Data Access Object DB 를 연결하여 입력/수정/삭제/조회 CRUD
	// Singleton 을 권장 (하나의 객체로 같이 사용), 정적 클래스 변수
	// static 은 객체를 생성하지 않고 클래스명.변수명으로 사용, 프로그램이 load 될 때 1회 실행
	private static MemberDao instance = new MemberDao();

	private MemberDao() {
	} // private 이므로 다른 클래스에서 객체 생성이 안됨
		// 다른 프로그램에서는 클래스명.메서드명(MemberDao.getInstance())로 객체 생성하여 사용

	public static MemberDao getInstance() {
		return instance;
	}

	// DataBase Connection pool
	private Connection getConnection() {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/OracleDB");
			conn = ds.getConnection();
		} catch (Exception e) {
			System.out.println("연결 에러 : " + e.getMessage());
		}
		return conn;
	}

	public int insert(Member member) {
		int result = 0;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		String sql = "insert into member1 values(?,?,?,sysdate)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getId());
			pstmt.setString(2, member.getPassword());
			pstmt.setString(3, member.getName());
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try { // close 하다가 에러 발생할 때 처리
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	public int loginChk(String id, String password) {
		int result = 0;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select password from member1 where id=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// password 는 화면에서 입력한 암호, dbPass 데이터베이스에서 읽은 암호
				String dbPass = rs.getString("password"); // dbPass 로 변수를 설정한 이유는 위에 화면에서 쓴 비밀번호 변수와 헷갈수도 있기때문에
				if (dbPass.equals(password))
					result = 1; // id 와 password 가 일치
				else
					result = 0; // id 는 맞지만 암호가 다르다
			} else
				result = -1; // ID에 해당하는 데이터가 없다
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {// close 하다가 에러 발생할 때 처리
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	public String getName(String id) {
		String name = "";
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select name from member1 where id=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// password 는 화면에서 입력한 암호, dbPass 데이터베이스에서 읽은 암호
				name = rs.getString("name"); // dbPass 로 변수를 설정한 이유는 위에 화면에서 쓴 비밀번호 변수와 헷갈수도 있기때문에
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {// close 하다가 에러 발생할 때 처리
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
		return name;
	}
}
