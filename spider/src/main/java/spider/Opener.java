package spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Opener {
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	public static CloseableHttpClient httpClient = HttpClients.custom()
			.setDefaultCookieStore(cookieStore).build();
	public static String login() throws ClientProtocolException, IOException{
		HashMap<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("username", "");
		dataMap.put("areacode", "86");
		dataMap.put("telephone", "18782071219");
		dataMap.put("remember_me", "1");
		dataMap.put("password", Encoding.MD5("199337").toUpperCase());
		HashMap<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("X-Requested-With", "XMLHttpRequest");
		return login("http://xueqiu.com/user/login",dataMap,headerMap);
	}
	public static String login(String url) throws ClientProtocolException, IOException {
		return login(url,null,null);
	}
	public static String login(String url,Map<String, String> dataMap) throws ClientProtocolException, IOException {
		return login(url,dataMap,null);
	}
	public static String login(String url,Map<String, String> dataMap,Map<String, String> headerMap) throws ClientProtocolException, IOException {
		HttpPost httppost = new HttpPost(url);
		if(dataMap!=null){
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> it = dataMap.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, String> entry = it.next();
				formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(entity);
		}
		if(headerMap!=null){
			Iterator<Entry<String, String>> it = headerMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				httppost.setHeader(entry.getKey(), entry.getValue());
			}
		}
		CloseableHttpResponse httpResponse = httpClient.execute(httppost);
        setCookieStore(httpResponse);
        HttpEntity entity1 = httpResponse.getEntity();
        if(entity1==null)return EntityUtils.toString(entity1, "UTF-8");
        else return "";
	}
	public static String get(String url) throws ParseException, IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null)return EntityUtils.toString(entity, "UTF-8");
        else return "";
	}
	private static void setCookieStore(HttpResponse httpResponse) {
		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		Pattern domainPattern = Pattern.compile("domain=([^\\;]+)");
		Pattern pathPattern = Pattern.compile("path=([^\\;]+)");
		for(Header header:headers){
			String string = header.getValue();
			String info = string.substring(0,string.indexOf(';'));
			String[] kv = info.split("=");
			if(kv.length>1){
				BasicClientCookie cookie = new BasicClientCookie(kv[0],kv[1]);
				cookie.setVersion(0);
				Matcher matcher = domainPattern.matcher(string);
				cookie.setDomain(matcher.find()?matcher.group(1):"127.0.0.1");
				matcher = pathPattern.matcher(string);
				cookie.setPath(matcher.find()?matcher.group(1):"/");
				cookieStore.addCookie(cookie);
			}
		}
	}
}
