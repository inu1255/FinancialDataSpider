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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.ParseException;

import tools.Mysql;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Spider {
	protected String url = "";
	public int count = 0;
	public void openUrl(String url) {
		++count;
		this.url = url;
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

	public void deal(String response) {

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
		//System.out.println("sql:"+sql);
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
	public String makeText(String html){
		return html.replaceAll("(<[^>]*>)|(&[a-z]+;)|(\\s+)", " ").trim();
	}
	public String makeUrl(String url){
		if(url.startsWith("http")){
			return url;
		}
		else if(url.startsWith("/")){
			return getHost()+url;
		}
		else if(url.startsWith("..")&&getPrevUrl().equals(getHost()+"/")){
			return getHost()+url.substring(2);
		}
		return getPrevUrl()+url;
	}
	public String getHost(){
		Pattern p = Pattern.compile("https?://[^/]+");
		Matcher m = p.matcher(url);
		if(m.find())
			return m.group();
		return "";
	}
	public String getPrevUrl(){
		Pattern p = Pattern.compile("https?://[\\s\\S]+/");
		Matcher m = p.matcher(url);
		if(m.find())
			return m.group();
		return "";
	}
}
