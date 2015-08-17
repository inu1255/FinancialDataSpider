package spider;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.PUBLIC_MEMBER;

public class BankSpider extends JsonSpider {
	@Override
	public void deal(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			OneBankSpider oneBankSpider = new OneBankSpider();
			int len = jsonArray.length();
			for(int i=0;i<len;++i){
				jsonObject = jsonArray.getJSONObject(i);
				System.out.println(i+"/"+len+":"+jsonObject.getString("symbol"));
				oneBankSpider.openUrl("http://xueqiu.com/v4/stock/quote.json?code=",jsonObject.getString("symbol"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		try {
			Opener.login();
			BankSpider bankSpiber = new BankSpider();
			bankSpiber.openUrl("http://xueqiu.com/financial_product/query.json?page=1&size=9999&order=desc&orderby=SALEBEGINDATE&status=1&_=1439607538138");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class OneBankSpider extends JsonSpider{
	private String id = "";
	public void openUrl(String url,String code) {
		id = code;
		openUrl(url+code);
	}
	@Override
	public String tableName(){
		return "bankproduct";
	}
	@Override
	public void deal(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			jsonObject = jsonObject.getJSONObject(id);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id",id);
			map.put("name",jsonObject.getString("name"));
			map.put("profit",jsonObject.getString("current"));
			map.put("dayLimit",jsonObject.getString("fp_act_entrust_period"));
			map.put("minMoney",jsonObject.getString("fp_entrust_start_amt"));
			map.put("moneyType",jsonObject.getString("fp_entrust_cur"));
			map.put("beginDate",jsonObject.getString("fp_sale_begin_date"));
			map.put("endDate",jsonObject.getString("fp_sale_end_date"));
			map.put("area",jsonObject.getString("fp_sale_area"));
			map.put("company",jsonObject.getString("fp_iss_banks_name"));
			map.put("profitType",jsonObject.getString("fp_return_get_mode"));
			map.put("increaseMoney",jsonObject.getString("fp_entrust_amt_add"));
			map.put("profitDate",jsonObject.getString("fp_return_start_date"));
			map.put("riskLevel",jsonObject.getString("fp_risk_level"));
			map.put("profitInstruction",jsonObject.getString("fp_yield_memo"));
			map.put("earlyTerminateCondition",jsonObject.getString("fp_early_end_condition"));
			map.put("scope",jsonObject.getString("fp_invest_range"));
			map.put("riskWarning",jsonObject.getString("fp_risk_memo"));
			insert(map);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}