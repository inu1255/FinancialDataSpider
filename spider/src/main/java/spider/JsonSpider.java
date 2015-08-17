package spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.ParseException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import database.Mysql;

public class JsonSpider {
	public void openUrl(String url) {
		String string;
		try {
			string = Opener.get(url);
			if(!string.isEmpty())
				deal(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deal(String str) {

	}

	public String tableName() {
		return "";
	}

	public void insert(Map map) {
		String sql = "insert `" + tableName() + "` (";
		String str = "";
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		boolean first = true;
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String val = entry.getValue();
			if(!val.isEmpty()){
				if (first) {
					sql += "`" + key + "`";
					str += "'" + val + "'";
					first = false;
				} else {
					sql += ",`" + key + "`";
					str += ",'" + val + "'";
				}
			}
		}
		sql = sql + ") values(" + str + ")";
		Statement statement = Mysql.getInstance();
		try {
			statement.execute(sql);
		}
		catch(MySQLIntegrityConstraintViolationException e) {
		}
		catch (SQLException e) {
			System.out.println("≤Â»Î ß∞‹:" + sql);
			e.printStackTrace();
		}
	}
}
