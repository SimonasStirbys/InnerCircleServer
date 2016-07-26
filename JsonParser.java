import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.*;
import org.json.simple.parser.*;

public class JsonParser {
	//static String jsonObjectTest = "{\"Requests\":[{\"Sender_ID\":5214,\"Recepient_name\":\"857\",\"Radius\":500,\"A0\":500,\"A1\":500,\"A2\":500,\"P\":10,\"Q\":5,\"gpowx\":15},{\"Sender_ID\":5214,\"Recepient_name\":\"857\",\"Radius\":500,\"A0\":500,\"A1\":500,\"A2\":500,\"P\":10,\"Q\":5,\"gpowx\":15}]}";
static ArrayList<String> receiver = new ArrayList<String>();
@SuppressWarnings("unchecked")
	public static ArrayList<String> parseRequestFromAlice(String Request) {
		JSONParser jsonParser = new JSONParser();
		
	
			JSONObject jsonObject;
			try {
				jsonObject = (JSONObject) jsonParser.parse(Request);
				JSONObject requests = (JSONObject) jsonObject.get("Requests");
				JSONArray nums=(JSONArray) requests.get("Recepient_ID");
				int index = 0;
				for(int i=0;i<nums.size();i++){
					
					JSONObject innerObj = (JSONObject)requests.get("Cred");
					JSONObject reqObj = new JSONObject();
					// System.out.println("Sender_Id " + innerObj.get("Sender_ID"));
					receiver.add(index,String.valueOf(nums.get(i)));
					reqObj.put("Request_Details", innerObj);
					receiver.add(index + 1, reqObj.toJSONString());
					index += 2;
			}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			
		return receiver;
	}
	public static String[] parseRequestFromBob(String answer){
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		String sendTo="";
		try {
			jsonObject = (JSONObject) jsonParser.parse(answer);
			JSONObject jsonAnswer = (JSONObject) jsonObject.get("Answer_Location");
			sendTo=jsonAnswer.get("Recepient_name").toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new String[]{sendTo,answer};
		
	}
	@SuppressWarnings("unchecked")
	public String[] userDetail(String message){
	String[] details=new String[2];
	try {
	JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj =(JSONObject)jsonParser.parse(message);
            JSONObject answer=(JSONObject)jsonObj.get("Registration");
            //Log.d("Answer in parser", answer.get("Answer").toString());
	    details[0]=answer.get("Sender_ID").toString();
	    details[1]=answer.get("Reg_ID").toString();
	} catch (ParseException e) {
            e.printStackTrace();
        }
	return details;
	}
	}
