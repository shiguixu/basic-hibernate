package org.casper.eLearning.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtil {
	/**
	 * 获取数据库的连接对象
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		Connection con = null;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", 
				"scott", "tiger");
		return con;
	}
	
	/**
	 * 关闭数据库的连接
	 * @param con
	 */
	public static void close(Connection con) {
		try {
			if(con!=null) con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭预编译
	 * @param ps
	 */
	public static void close(PreparedStatement ps) {
		try {
			if(ps!=null) ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭结果集
	 * @param rs
	 */
	public static void close(ResultSet rs) {
		try {
			if(rs!=null) rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
