package spider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Log;
import tools.Mysql;

public class BankRealProfit extends Spider {
	private String company = "";
	private String alistKeyWords = ""; 
	private String profitKeyWords = "";
	private String content = "";
	public void openUrl(String url,String c,String a,String p,String o) {
		company = c;
		alistKeyWords =a;
		profitKeyWords=p;
		content = o;
		openUrl(url);
	}
	@Override
	public void deal(String html){
		if(content.isEmpty())
			firstPageNameSecondPageValue(html);
		else if(content.startsWith("http"))
			firstPageJsonSecondPageValue(html);
		else
			secondPageNameAndValue(html);
	}
	public void firstPageJsonSecondPageValue(String html) {
		Log.info("��:");
		Pattern p = Pattern.compile(alistKeyWords);
		Matcher m = p.matcher(html);
		OneBankRealProfit realProfit = new OneBankRealProfit(company,profitKeyWords);
		while (m.find()) {
			String url = content+m.group("id");
			String name= m.group("name");
			Log.info("����ڶ�ҳ:"+name+"-->"+url);
			realProfit.openUrl(url, name);
		}
	}
	// ��һҳ����Ŀ�б�������ȡ�����Ŀ����Ŀ����ʵ��������
	public void secondPageNameAndValue(String html) {
		Log.info("��:");
		Pattern p = Pattern.compile("href=[\"']([^\"']+)[\"'][^<]*(>[^<]*"+alistKeyWords+"|\"[^\"]*"+alistKeyWords+")");
		Matcher m = p.matcher(html);
		TwoBankRealProfit realProfit = new TwoBankRealProfit(company,profitKeyWords,content);  
		while (m.find()) {
			String url = m.group(1);
			Log.info(makeUrl(url));
			realProfit.openUrl(makeUrl(url));
		}
	}
	// ��һҳ����Ŀ�б�������Ŀ����������ȡ��Ӧ��Ŀ��ʵ��������
	public void firstPageNameSecondPageValue(String html) {
		Log.info("һ:");
		Pattern p = Pattern.compile("href=[\"']([^\"']+)[\"'][^<]*>\\s*([^<]*"+alistKeyWords+"[^<\\s]*)");
		Matcher m = p.matcher(html);
		OneBankRealProfit realProfit = new OneBankRealProfit(company,profitKeyWords);
		boolean has = false;
		while (m.find()) {
			has = true;
			String url = m.group(1);
			String name= m.group(2);
			Log.info("����ڶ�ҳ:"+name+"-->"+makeUrl(url));
			realProfit.openUrl(makeUrl(url), name);
		}
		if(!has)Log.errer(url+":"+"href=[\"']([^\"']+)[\"'][^<]*>\\s*([^<]*"+alistKeyWords+"[^<\\s]*)");
	}
	public static void main(String[] args) {
		Statement statement = Mysql.getInstance();
		//String sql = "select * from bankinfo where announcementUrl!='' and alistKeyWords!='' and profitKeyWords!='' and content=''";
		String sql = "select * from bankinfo where id=3";
		try {
			statement.execute("TRUNCATE bankrealprofit_new");
			statement.execute("alter table bankrealprofit_new auto_increment=1");
			statement.executeQuery(sql);
			ResultSet resultSet = statement.getResultSet();
			BankRealProfit bankRealProfit = new BankRealProfit();
			Log.info("-->start");
			while (resultSet.next()) {
				String announcementUrl = resultSet.getString("announcementUrl");
				String company = resultSet.getString("name");
				String alistKeyWords = resultSet.getString("alistKeyWords");
				String profitKeyWords = resultSet.getString("profitKeyWords");
				String content = resultSet.getString("content");
				Log.info("begin::"+company+"-->"+announcementUrl);
				bankRealProfit.openUrl(announcementUrl,company, alistKeyWords, profitKeyWords,content);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class OneBankRealProfit extends Spider{
	private String name = "";
	private String keyWords = "";
	private String company  = "";
	public OneBankRealProfit(String company,String profitKeyWords) {
		this.company  = company;
		this.keyWords = profitKeyWords;
	}
	public void openUrl(String url,String name) {
		this.name = name;
		openUrl(url);
	}
	@Override
	public String tableName(){
		return "bankrealprofit_new";
	}
	@Override
	public void deal(String html){
		Pattern p;
		html = html.replaceAll("<[^>]*?>", "");
		if(keyWords.startsWith("regex"))p= Pattern.compile(keyWords.substring(5));
		else p= Pattern.compile(keyWords+"[\\s\\S]*?((\\d+\\.\\d+)|(\\d+))%");
		Matcher m = p.matcher(html);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("company", company);
		map.put("url", url);
		map.put("name", name);
		if (m.find()) {
			Log.info("�ҵ�ʵ������:"+m.group(1));
			map.put("realprofit", m.group(1));
		}
		else if(html.indexOf("��ʵ����ƺ�ͬ�е�Ԥ������껯������")>=0){
			Log.info("�ҵ�ʵ������:��ʵ��");
			map.put("realprofit", "100");
		}
		else{
			Log.errer("û��ʵ������:"+company+":"+name+"-->"+url);
			map.put("realprofit", "-1");
		}
		insert(map);
	}
}
class TwoBankRealProfit extends Spider{
	private String name = "";
	private String keyWords = "";
	private String company  = "";
	private String content  = "";
	public TwoBankRealProfit(String company,String profitKeyWords,String content) {
		this.company  = company;
		this.keyWords = profitKeyWords;
		this.content  = content;
	}
	@Override
	public String tableName(){
		return "bankrealprofit_new";
	}
	@Override
	public void deal(String html){
		Pattern p = Pattern.compile(content);
		Matcher m = p.matcher(html);
		while(m.find()){
			Log.info("content:");
			extractNameValue(m.group());
		}
	}
	private void extractNameValue(String group) {
		Pattern p = Pattern.compile(keyWords);
		Matcher m = p.matcher(group);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("company", company);
		map.put("url", url);
		while(m.find()){
			String name = makeText(m.group("name"));
			map.put("name",name);
			map.put("realprofit",m.group("value"));
			Log.info("�ҵ�һ������:"+name+"-->"+m.group("value"));
			insert(map);
		}
	}
}