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

import tools.Mysql;

public class HTMLSpiber extends Spider {
	private String url = "";
	@Override
    public void openUrl(String url){
		this.url = url;
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
    public static void main(String[] args) {
		HTMLSpiber spiber = new HTMLSpiber();
		spiber.openUrl("http://www.cib.com.cn/cn/Financing_Release/dqcp/index.html");
	}
}
