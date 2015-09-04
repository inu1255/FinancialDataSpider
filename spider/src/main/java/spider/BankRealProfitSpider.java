package spider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.Log;

public class BankRealProfitSpider extends Spider {
	public String id = "";
	public String company = "";
	public String announcementUrl = "";
	public String alistKeyWords = ""; 
	public String profitKeyWords = "";
	public String content = "";
	public String name = "";
	public BankRealProfitSpider(String id,String company,String announcementUrl,String alistKeyWords,String profitKeyWords,String content) {
		this.id = id;
		this.company = company;
		this.announcementUrl = announcementUrl;
		this.alistKeyWords = alistKeyWords;
		this.profitKeyWords = profitKeyWords;
		this.content = content;
		openUrl(announcementUrl);
	}
	@Override
	public String tableName(){
		return "bankrealprofit_new";
	}
	@Override
	public void deal(String html){
		if(count==1)
			getTitleAndUrl(html);
		else
			getNameAndRealprofit(html);
	}
	public void getTitleAndUrl(String html) {
		if(content.isEmpty()){
			Log.info("一:");
			firstPageNameSecondPageValue(html);
		}
		else if(content.startsWith("http")){
			Log.info("二:");
			firstPageJsonSecondPageValue(html);
		}
		else{
			Log.info("三:");
			secondPageNameAndValue(html);
		}
	}
	public void firstPageJsonSecondPageValue(String html) {
		Pattern p = Pattern.compile(alistKeyWords);
		Matcher m = p.matcher(html);
		OneBankRealProfit realProfit = new OneBankRealProfit(company,profitKeyWords);
		while (m.find()) {
			String url = content+m.group("id");
			String name= m.group("name");
			Log.info("进入第二页:"+name+"-->"+url);
			realProfit.openUrl(url, name);
			if(realProfit.count>30)break;
		}
	}
	// 第一页是项目列表，进入后获取多个项目的项目名和实际收益率
	public void secondPageNameAndValue(String html) {
		Pattern p = Pattern.compile("href=[\"']([^\"']+)[\"'][^<]*(>[^<]*"+alistKeyWords+"|\"[^\"]*"+alistKeyWords+")");
		Matcher m = p.matcher(html);
		TwoBankRealProfit realProfit = new TwoBankRealProfit(company,profitKeyWords,content);  
		while (m.find()) {
			String url = m.group(1);
			Log.info(makeUrl(url));
			realProfit.openUrl(makeUrl(url));
			if(realProfit.count>30)break;
		}
	}
	// 第一页是项目列表，包含项目名，进入后获取对应项目的实际收益率
	public void firstPageNameSecondPageValue(String html) {
		Pattern p;
		if(alistKeyWords.startsWith("regex")) p = Pattern.compile(alistKeyWords.substring(5));
		else p = Pattern.compile("href=[\"'](?<url>[^\"']+)[\"'][^<]*>\\s*(?<name>[^<]*"+alistKeyWords+"[^<\\s]*)");
		Matcher m = p.matcher(html);
		OneBankRealProfit realProfit = new OneBankRealProfit(company,profitKeyWords);
		while (m.find()) {
			String url = m.group("url");
			String name= m.group("name");
			Log.info("进入第二页:"+name+"-->"+makeUrl(url));
			realProfit.openUrl(makeUrl(url), name);
			if(realProfit.count>30)break;
		}
		if(realProfit.count<1)Log.errer(url+":"+"href=[\"']([^\"']+)[\"'][^<]*>\\s*([^<]*"+alistKeyWords+"[^<\\s]*)");
	}
	public void getNameAndRealprofit(String html) {
		
	}
}
