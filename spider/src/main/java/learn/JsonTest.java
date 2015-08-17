package learn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class JsonTest {
	public static void test1() throws JSONException{
		String myString = new JSONStringer().object()     
				.key("name")    
				.value("小猪")     
				.endObject()    
				.toString();
		System.out.println(myString);
	}
	public static void main(String[] args) throws JSONException {
		//接收到的JSON字符串
		String result = "[{\"username\": \"your name\", \"user_json\": {\"username\": \"your name\", \"nickname\": \"your nickname\"}}]";

		//根据字符串生成JSON对象
		JSONArray resultArray = new JSONArray(result);
		JSONObject resultObj = resultArray.optJSONObject(0);

		//获取数据项
		String username = resultObj.getString("username");

		//获取数据对象
		JSONObject user = resultObj.getJSONObject("user_json");
		String nickname = user.getString("nickname");
		System.out.println(username+":"+user+":"+nickname);
	}
}
