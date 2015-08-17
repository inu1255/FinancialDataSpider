package learn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import spider.Encoding;

public class HttpClientTest2 {
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	public static CloseableHttpClient httpClient = HttpClients.custom()
			.setDefaultCookieStore(cookieStore).build();

	public static void test1() throws ClientProtocolException, IOException {
		//httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("username", ""));
		formparams.add(new BasicNameValuePair("areacode", "86"));
		formparams.add(new BasicNameValuePair("telephone", "18782071219"));
		formparams.add(new BasicNameValuePair("remember_me", "1"));
		formparams.add(new BasicNameValuePair("password",Encoding.MD5("199337").toUpperCase()));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		HttpPost httppost = new HttpPost("http://xueqiu.com/user/login");
		httppost.setEntity(entity);
		httppost.setHeader("X-Requested-With", "XMLHttpRequest");
		CloseableHttpResponse httpResponse = httpClient.execute(httppost);
        HttpEntity entity1 = httpResponse.getEntity();
        if (entity1 != null) {  
            System.out.println("--------------------------------------");  
            System.out.println("Response content: " + EntityUtils.toString(entity1, "UTF-8"));  
            System.out.println("--------------------------------------");  
        }
        setCookieStore(httpResponse);
        HttpGet httpGet = new HttpGet("http://xueqiu.com/financial_product/query.json?page=1&size=3&order=desc&orderby=SALEBEGINDATE&status=1&_=1439607538138");

		httpResponse = httpClient.execute(httpGet);
        entity1 = httpResponse.getEntity();
        if (entity1 != null) {  
            System.out.println("--------------------------------------");  
            System.out.println("Response content: " + EntityUtils.toString(entity1, "UTF-8"));  
            System.out.println("--------------------------------------");  
        }
	}

	public static void setCookieStore(HttpResponse httpResponse) {
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

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		test1();
	}
}
