package tools;

import java.sql.*;

public class Mysql {
	private Connection con = null;
	private static Mysql mysql = null;
	private Mysql(String db,String user,String password,String host,String port){
		connect("jdbc:mysql://"+host+":"+port+"/"+db+"?user="+user+"&password="+password+"&useUnicode=true&characterEncoding=UTF-8");
	}
	public static Statement getInstance(String db,String user,String password,String host,String port){
		if(mysql==null||mysql.con==null){
			mysql = new Mysql(db, user, password,host, port);
		}
		try {
			return mysql.con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Statement getInstance(){
		if(mysql==null||mysql.con==null){
			mysql = new Mysql("sms","root","","127.0.0.1","3306");
		}
		try {
			return mysql.con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private void connect(String config){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(config);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
