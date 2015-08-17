package learn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class JsonTest {
	public static void test1() throws JSONException{
		String myString = new JSONStringer().object()     
				.key("name")    
				.value("С��")     
				.endObject()    
				.toString();
		System.out.println(myString);
	}
	public static void main(String[] args) throws JSONException {
		//���յ���JSON�ַ���
		String result = "[{\"username\": \"your name\", \"user_json\": {\"username\": \"your name\", \"nickname\": \"your nickname\"}}]";

		//�����ַ�������JSON����
		JSONArray resultArray = new JSONArray(result);
		JSONObject resultObj = resultArray.optJSONObject(0);

		//��ȡ������
		String username = resultObj.getString("username");

		//��ȡ���ݶ���
		JSONObject user = resultObj.getJSONObject("user_json");
		String nickname = user.getString("nickname");
		System.out.println(username+":"+user+":"+nickname);
	}
}
