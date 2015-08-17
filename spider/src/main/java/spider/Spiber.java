package spider;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import database.Mysql;

public class Spiber {
    public void openUrl(String url){
        Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(doc!=null){
			deal(doc);
		}
		else {
			System.out.println("´ò¿ªÍøÒ³Ê§°Ü:"+url);
		}
    }
    public void deal(Document doc){
    }
    public String tableName() {
		return "";
	}
	public void insert(Map map){
    	String sql = "insert `"+tableName()+"` (";
    	String str = "";
    	Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
    	boolean first = true;
    	while (it.hasNext()) {
			Map.Entry<String,String> entry = it.next();
			String key = entry.getKey();
			String val = entry.getValue();
			if(first){
				sql += "`"+key+"`";
				str += "'"+val+"'";
				first = false;
			}
			else {
				sql += ",`"+key+"`";
				str += ",'"+val+"'";
			}
		}
    	sql = sql+") values("+str+")";
    	Statement statement = Mysql.getInstance();
    	try {
			statement.execute(sql);
		} catch (SQLException e) {
			System.out.println("²åÈëÊ§°Ü:"+sql);
			e.printStackTrace();
		}
    }
}
