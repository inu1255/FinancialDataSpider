package spider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Log;
import tools.Mysql;

public class BankRealProfit extends Spider {
	private String id = "";
	private String company = "";
	private String alistKeyWords = ""; 
	private String profitKeyWords = "";
	private String content = "";
	private String lastUrl = "";
	private String newLastUrl = null;
	private boolean isIframe = false;
	public void openUrl(String i,String url,String c,String a,String p,String o,String l) {
		id = i;
		company = c;
		alistKeyWords =a;
		profitKeyWords=p;
		content = o;
		lastUrl = l;
		if(url.startsWith("i")){
			isIframe = true;
			url = url.substring(1);
		}
		else{
			isIframe = false;
		}
		openUrl(url);
	}
	@Override
	public void deal(String html){
		if(isIframe){
			findIframe(html);
		}
		else{
			findLists(html);
		}
	}
	public void findIframe(String html){
		Pattern p = Pattern.compile("(?i)<iframe[^>]+src=['\"]?([^ '\"]+)");
		Matcher m = p.matcher(html);
		if(m.find()){
			String url = makeUrl(m.group(1));
			Log.info("�����б����iframe:"+url);
			isIframe = false;
			openUrl(url);
		}
	}
	private void findLists(String html) {
		if(content.isEmpty())
			firstPageNameSecondPageValue(html);
		else if(content.startsWith("http"))
			firstPageJsonSecondPageValue(html);
		else
			secondPageNameAndValue(html);
		if(null!=newLastUrl)updateLastUrl(newLastUrl);
	}
	// ��һҳͨ��json��ȡ����
	public void firstPageJsonSecondPageValue(String html) {
		Log.info("��:");
		Pattern p;
		OneBankRealProfit realProfit = new OneBankRealProfit(company,profitKeyWords);
		if(alistKeyWords.startsWith("order")){
			p = Pattern.compile(alistKeyWords.substring(5));
			Matcher m = p.matcher(html);
			Stack<String> stack = new Stack<String>();
			while (m.find()) {
				String url = content+m.group("id");
				String name= m.group("name");
				stack.push(url);
				stack.push(name);
			}
			while (!stack.isEmpty()) {
				String name= stack.pop();
				String url = stack.pop();
				if(checkLastUrl(realProfit, url))break;
				Log.info("����ڶ�ҳ:"+name+"-->"+url);
				realProfit.openUrl(url, name);
				if(realProfit.count>30)break;
			}
		}
		else{
			p = Pattern.compile(alistKeyWords);
			Matcher m = p.matcher(html);
			while (m.find()) {
				String url = content+m.group("id");
				if(checkLastUrl(realProfit, url))break;
				String name= m.group("name");
				Log.info("����ڶ�ҳ:"+name+"-->"+url);
				realProfit.openUrl(url, name);
				if(realProfit.count>30)break;
			}
		}
		if(realProfit.count<1)Log.errer("û��Jsonƥ��-->"+url+":"+alistKeyWords);
	}
	// ��һҳ����Ŀ�б�������ȡ�����Ŀ����Ŀ����ʵ��������
	public void secondPageNameAndValue(String html) {
		Log.info("��:");
		Matcher m = makeMacher(html, "href=[\"'](?<url>[^\"']+)[\"'][^<]*(?<name>>[^<]*"+alistKeyWords+"|\"[^\"]*"+alistKeyWords+")");
		TwoBankRealProfit realProfit = new TwoBankRealProfit(company,profitKeyWords,content);
		while (m.find()) {
			String url = makeUrl(m.group("url"));
			if(checkLastUrl(realProfit, url))break;
			Log.info(url);
			realProfit.openUrl(url);
			if(realProfit.count>30)break;
		}
	}
	// ��һҳ����Ŀ�б�������Ŀ����������ȡ��Ӧ��Ŀ��ʵ��������
	public void firstPageNameSecondPageValue(String html) {
		Log.info("һ:");
		String def = "href=[\"'](?<url>[^\"']+)[\"'][^<]*(?<name>(?<=>)[^<]*"+alistKeyWords+"[^<]*|(?<=\")[^\"]*"+alistKeyWords+"[^\"]*)";
		Matcher m = makeMacher(html, def);
		OneBankRealProfit realProfit = new OneBankRealProfit(company,profitKeyWords);
		while (m.find()) {
			String url = makeUrl(m.group("url"));
			if(checkLastUrl(realProfit, url))break;
			String name= m.group("name");
			Log.info("����ڶ�ҳ:"+name+"-->"+url);
			realProfit.openUrl(url, name);
			if(realProfit.count>30)break;
		}
		if(realProfit.count<1)Log.errer("û��ƥ��-->"+url+":"+def);
	}
	private Matcher makeMacher(String html,String def) {
		Pattern p;
		if(alistKeyWords.startsWith("regex")) p = Pattern.compile(alistKeyWords.substring(5));
		else if(null==def) p = Pattern.compile(alistKeyWords);
		else p = Pattern.compile(def);
		return p.matcher(html);
	}
	/**
	 * �����ж��ظ� ����ÿ���ظ���ȡ
	 * @param spider
	 * @param url
	 * @return
	 */
	private boolean checkLastUrl(Spider spider,String url){
//		if(url.equals(lastUrl))return true;
//		if(spider.count==0)newLastUrl = url;
		return false;
	}
	private void updateLastUrl(String newLastUrl) {
		if(null==newLastUrl)return;
		Statement statement = Mysql.getInstance();
		try {
			statement.execute("update bankinfo set lastUrl = '"+newLastUrl+"' where id='"+id+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		newLastUrl = null;
	}
	public static void main(String[] args) {
		Statement statement = Mysql.getInstance();
		//String sql = "select * from bankinfo where announcementUrl!='' and alistKeyWords!='' and profitKeyWords!='' and content=''";
		String sql = "select * from bankinfo where id>=154";
		try {
			statement.execute("TRUNCATE bankrealprofit_new");
			statement.execute("alter table bankrealprofit_new auto_increment=1");
			statement.executeQuery(sql);
			ResultSet resultSet = statement.getResultSet();
			BankRealProfit bankRealProfit = new BankRealProfit();
			Log.info("-->start");
			while (resultSet.next()) {
				String id = resultSet.getString("id");
				String announcementUrl = resultSet.getString("announcementUrl");
				String company = resultSet.getString("name");
				String alistKeyWords = resultSet.getString("alistKeyWords");
				String profitKeyWords = resultSet.getString("profitKeyWords");
				String content = resultSet.getString("content");
				String lastUrl = resultSet.getString("lastUrl");
				Log.info("begin::"+company+"-->"+announcementUrl);
				bankRealProfit.openUrl(id,announcementUrl,company, alistKeyWords, profitKeyWords,content,lastUrl);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
/**
 * ��һ��ҳ����ͨ��realProfitKeywords��ȡһ��  ʵ������
 * @author inu1255
 * name �ڻ�ȡ��Ŀ�б�ʱ�Ѿ����
 */
class OneBankRealProfit extends Spider{
	private String name = "";
	private String keyWords = "";
	private String company  = "";
	private String profitKeyWords = "";
	public OneBankRealProfit(String company,String profitKeyWords) {
		this.company  = company;
		this.profitKeyWords = profitKeyWords;
	}
	public void openUrl(String url,String name) {
		this.name = name;
		this.keyWords = profitKeyWords;
		openUrl(url);
	}
	@Override
	public String tableName(){
		return "bankrealprofit_new";
	}
	@Override
	public void deal(String html){
		// ��i��ͷ����ҳ������ iframe
		if(keyWords.startsWith("i")){
			keyWords = keyWords.substring(1);
			findIframe(html);
		}
		else{
			findValue(html);
		}
	}
	public void findIframe(String html){
		Pattern p = Pattern.compile("(?i)<iframe[^>]+src=['\"]?([^ '\"]+)");
		Matcher m = p.matcher(html);
		if(m.find()){
			String url = makeUrl(m.group(1));
			Log.info("����iframe:"+url);
			openUrl(url);
		}
		else{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("company", company);
			map.put("url", url);
			map.put("name", makeText(name));
			map.put("realprofit", "-1");
			Log.errer("û��ʵ������:"+company+":"+name+"-->"+url);
			insert(map);
		}
	}
	private void findValue(String html) {
		Pattern p;
		if(keyWords.startsWith("regex"))p= Pattern.compile(keyWords.substring(5));
		else {
			html = html.replaceAll("<[^>]*?>", "");
			p= Pattern.compile(keyWords+"[\\s\\S]*?(?<value>(\\d+\\.\\d+)|(\\d+))[��%]");
		}
		Matcher m = p.matcher(html);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("company", company);
		map.put("url", url);
		map.put("name", makeText(name));
		if (m.find()) {
			Log.info("�ҵ�ʵ������:"+m.group("value"));
			map.put("realprofit", m.group("value"));
		}
		else if(html.indexOf("��ʵ����ƺ�ͬ�е�Ԥ������껯������")>=0){
			Log.info("�ҵ�ʵ������:��ʵ��");
			map.put("realprofit", "1255");
		}
		else{
			Log.errer("û��ʵ������:"+company+":"+name+"-->"+url);
			map.put("realprofit", "-1");
		}
		insert(map);
	}
}
/**
 * ͨ�� content �޶���Χ����ͨ��realProfitKeywords���Ҷ���   ��Ŀ����ʵ������
 * @author inu1255
 * ��� content��Ϊ���Ҳ���http��ͷ��ʹ�ñ���
 */
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
	/**
	 * ͨ��content��ȡ�޶�������
	 */
	public void deal(String html){
		Pattern p = Pattern.compile(content);
		Matcher m = p.matcher(html);
		while(m.find()){
			Log.info("content:");
			extractNameValue(m.group());
		}
	}
	/**
	 * ͨ�� keyWordsƥ����� ��Ŀ��:ʵ������
	 * @param group
	 */
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