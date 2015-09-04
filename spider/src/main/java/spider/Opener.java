package spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class Opener {
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	public static CloseableHttpClient httpClient = HttpClients.custom()
			.addInterceptorLast(new HttpRequestInterceptor() {
				public void process(HttpRequest request, HttpContext context)
						throws HttpException, IOException {
					Header header = request.getLastHeader("Host");
					if(header!=null&&"www.cib.com.cn".equals(header.getValue()))
						request.removeHeaders("Accept-Encoding");
				}
			}).setDefaultCookieStore(cookieStore).build();

	public static String login() throws ClientProtocolException, IOException {
		HashMap<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("username", "");
		dataMap.put("areacode", "86");
		dataMap.put("telephone", "18782071219");
		dataMap.put("remember_me", "1");
		dataMap.put("password", Encoding.MD5("199337").toUpperCase());
		HashMap<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("X-Requested-With", "XMLHttpRequest");
		return login("http://xueqiu.com/user/login", dataMap, headerMap);
	}

	public static String login(String url) throws ClientProtocolException,
			IOException {
		return login(url, null, null);
	}

	public static String login(String url, Map<String, String> dataMap)
			throws ClientProtocolException, IOException {
		return login(url, dataMap, null);
	}

	public static String login(String url, Map<String, String> dataMap,
			Map<String, String> headerMap) throws ClientProtocolException,
			IOException {
		HttpPost httppost = new HttpPost(encodeURL(url.trim()));
		if (dataMap != null) {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> it = dataMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				formparams.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			httppost.setEntity(entity);
		}
		if (headerMap != null) {
			Iterator<Entry<String, String>> it = headerMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				httppost.setHeader(entry.getKey(), entry.getValue());
			}
		}
		CloseableHttpResponse httpResponse = httpClient.execute(httppost);
		setCookieStore(httpResponse);
		return getContent(httpResponse);
	}

	public static String get(String url) throws ParseException, IOException {
		HttpGet httpGet = new HttpGet(encodeURL(url.trim()));
		HttpResponse response = httpClient.execute(httpGet);
		return getContent(response);
	}
	public static String getContent(HttpResponse response) throws IOException  {
		HttpEntity entity = response.getEntity();
		if(entity == null)return "";
		Header contentType = entity.getContentType();
		if(contentType!=null&&contentType.getValue().indexOf("charset")>=0){
			return EntityUtils.toString(entity, "UTF-8");
		}
		byte[] b = EntityUtils.toByteArray(entity);
		String tmp = new String(b);
		Pattern p = Pattern.compile("<meta[^>]+charset=[\"']?([\\w\\-]+)");
		Matcher m = p.matcher(tmp);
		if(m.find()){
            String res = new String(b,m.group(1));//这里写转换后的编码方式
            return res;
		}
		return new String(b,"UTF-8");
	}
	private static void setCookieStore(HttpResponse httpResponse) {
		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		Pattern domainPattern = Pattern.compile("domain=([^\\;]+)");
		Pattern pathPattern = Pattern.compile("path=([^\\;]+)");
		for (Header header : headers) {
			String string = header.getValue();
			String info = string.substring(0, string.indexOf(';'));
			String[] kv = info.split("=");
			if (kv.length > 1) {
				BasicClientCookie cookie = new BasicClientCookie(kv[0], kv[1]);
				cookie.setVersion(0);
				Matcher matcher = domainPattern.matcher(string);
				cookie.setDomain(matcher.find() ? matcher.group(1)
						: "127.0.0.1");
				matcher = pathPattern.matcher(string);
				cookie.setPath(matcher.find() ? matcher.group(1) : "/");
				cookieStore.addCookie(cookie);
			}
		}
	}

	public static String encodeURL(String url) {
		try {
			int index = url.indexOf('?') + 1;
			if (index > 0)
				url = url.substring(0, index)
						+ URLEncoder.encode(url.substring(index), "UTF-8")
								.replaceAll("%3D", "=").replaceAll("%26", "&");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

	public static void main(String[] args) {
		String url = "http://www.lj-bank.com/list.php?tid&8=";
		System.out.println(encodeURL(url));
	}
}
