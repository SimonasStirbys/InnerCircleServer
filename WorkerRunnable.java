import java.io.*;
import java.net.Socket;
import java.util.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.sql.Timestamp;

public class WorkerRunnable implements Runnable {

	protected Socket clientSocket = null;
	protected String serverText = null;
	BufferedReader input=null;
	PrintWriter output=null;
	String messageFromUser = "";
	JsonParser parser=null ;
	HashMap users;
	ArrayList<User> messages;
	public WorkerRunnable(Socket clientSocket, String serverText,HashMap users,ArrayList messages) {
		this.clientSocket = clientSocket;
		this.serverText = serverText;
		this.users=users;
		this.messages=messages;
		parser=new JsonParser();
	}

	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new PrintWriter(clientSocket.getOutputStream(), true);
			long time = System.currentTimeMillis();
			java.util.Date date= new java.util.Date();
			
				while(true){
			messageFromUser = input.readLine();
			
			if(messageFromUser.contains("Requests")){
			System.out.println("Message from user: "+ messageFromUser);			
	ArrayList<String> locReq=parser.parseRequestFromAlice(messageFromUser);
			for(int i=0;i<locReq.size();i+=2){
				sendMessage(locReq.get(i),locReq.get(i+1));
				}
			System.out.println(new Timestamp(date.getTime()));
			locReq.clear();
									
					break;	
			}else if(messageFromUser.contains("Answer")){
				String[] answer=parser.parseRequestFromBob(messageFromUser);
				User user= new User(answer[0],answer[1]);
				messages.add(user);
				String msg="{\"Answer_Location\":{\"Message_Received\":\"Yes\"}}";
				sendMessage(answer[0],msg);
			System.out.println(new Timestamp(date.getTime()));
				break;
			}else if(messageFromUser.contains("Registration")){
					System.out.println("Message from user: "+ messageFromUser);
				String[] details=parser.userDetail(messageFromUser);
				users.put(details[0],details[1]);
				output.println("Registrations Successfull");
					System.out.println(new Timestamp(date.getTime()));
				break;
			}else if(messageFromUser.contains("Check")){
				System.out.println("Message from user: "+ messageFromUser);
				String name=messageFromUser.substring(messageFromUser.indexOf(":")+2,messageFromUser.length()-2);
				System.out.println("Name is "+name);
				System.out.println(new Timestamp(date.getTime()));
				for(int i=0;i<messages.size();i++){
						if(messages.get(i).getName().equals(name)){	
				output.println((String)messages.get(i).getMessage());
				messages.remove(i);
				System.out.println("Found");			
				
				break;
			}
				break;
			
			}
			}
			}
//			output.println(("HTTP/1.1 200 OK  WorkerRunnable: " + this.serverText + " - " + time + ""));
			//output.close();
			input.close();
			System.out.println("Request processed: " + time);
		} catch (IOException e) {
			// report exception somewhere.
			e.printStackTrace();
		}
	}
	public void sendAnswer(String message){
	System.out.println("answer To send " + message);
	output.println(message);
	output.close();
	}
	public void sendMessage(String recpID,String message)throws IOException {
		String url = "https://gcm-http.googleapis.com/gcm/send";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("content-type", "application/json");
		con.setRequestProperty("Authorization", "key=AIzaSyBICJntQ-eaXG4Zx0kL19Y97mYjomSWzrE");
		con.setDoOutput(true);
	String params = "{\"time_to_live\":500,\"data\" :"+message+",\"to\":\""+users.get(recpID)+"\"}";		
	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(params);
			wr.flush();
			wr.close();
		System.out.println("Message in send "+params);
		System.out.println("Resp Code:" + con.getResponseCode());
		System.out.println("Resp Message:" + con.getResponseMessage());
					
		}
	}
