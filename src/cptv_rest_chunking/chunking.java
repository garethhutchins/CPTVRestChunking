package cptv_rest_chunking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;











import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



















import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;



public class chunking {
	public String ticket;
	//Here are the JSON request and response objects that we'll handle
	private class LoginRequest {
		public String culture = "en-US";
		public String licenseKey = "";
		public String deviceId = "REST Demo";
		public String applicationId = "";
		public String username;
		public String password;
	}
	private class LoginResponse {
		public returnStatus returnStatus;
		public String ticket;
	}
	private class returnStatus {
		public Integer status;
		public String code;
		public String message;
		public String server;
	}
	private class uploadrequest {
		String data;
		String contentType;
		Integer offset;
	}
	private class uploadResponse {
		public returnStatus returnStatus;
		public String id;
		public String contentType;
		public String src;
		public String updated;
	}
	public String GetTicket(String URL, String username,String password) {

		LoginRequest NewLogin = new LoginRequest();
		NewLogin.username = username;
		NewLogin.password = password;
		
		//Now change the request to JSON
		Gson gson = new Gson();
		String json = gson.toJson(NewLogin);
		
		//Now create the http post
		DefaultHttpClient client = new DefaultHttpClient();
		URL = URL + "/session";
		HttpPost postrequest = new HttpPost(URL);
		postrequest.addHeader("Content-Type","application/vnd.emc.captiva+json; charset=utf-8");
		try {
			postrequest.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
			//Now try to post the request
			HttpResponse response = null;
			String strResponse = "";
			response = client.execute(postrequest);
			strResponse = EntityUtils.toString(response.getEntity(), "UTF8");
			Gson ds = new Gson();
			LoginResponse LResponse = new LoginResponse();
			LResponse = ds.fromJson(strResponse, LoginResponse.class);
			ticket = LResponse.ticket;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return ticket;
	}
	
	public String UploadFile(String URL, String ticket, String FilePath) {
		//This is the function for chunking the file
		String FileID = "";
		Integer fileSize = null;
		//Path to the file
		File ifile = new File(FilePath);
		FileInputStream fis;
		//This is the size of the chunks that we're going to use in this example
		Integer ChunkSize = 100000;
		Integer readLength = ChunkSize;
		Integer read = 0;
		Integer nChunks = 0;
		Integer BytesRemaining;
		Integer BytesSent = 0;
		byte[] byteChunk;
		try {
			fis = new FileInputStream(ifile);
			fileSize = (int) ifile.length();
			BytesRemaining = fileSize;
			URL = URL + "/session/files";
			while (BytesRemaining > 0) {
				if (BytesRemaining <= ChunkSize){
					readLength = BytesRemaining;
				}
				byteChunk = new byte[readLength];
				read = fis.read(byteChunk, 0, readLength);
				BytesRemaining -= read;
				assert(read==byteChunk.length);
				nChunks++;
				//Now create the http post
				DefaultHttpClient client = new DefaultHttpClient();
				
				HttpPost postrequest = new HttpPost(URL);
				//Add the Headers
				postrequest.addHeader("Cookie","CPTV-TICKET=" + ticket);
				postrequest.addHeader("Content-Range","bytes " + BytesSent + "-" + (BytesSent + readLength -1) + "/" + fileSize.toString());
				//We'll hardcode the content type here just for an example
				postrequest.addHeader("Content-Type","image/tiff");
				postrequest.setEntity(new ByteArrayEntity(byteChunk));
				//Now try to post the request
				HttpResponse response = null;
				String strResponse = "";
				response = client.execute(postrequest);
				strResponse = EntityUtils.toString(response.getEntity(), "UTF8");
				
				BytesSent = BytesSent + readLength;
				//Get the new URL
				Gson ds = new Gson();
				uploadResponse UR = new uploadResponse();
				UR = ds.fromJson(strResponse, uploadResponse.class);
				URL = UR.src;
				FileID = UR.id;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return FileID;
		
	}
	
}
