package cptv_rest_chunking;

import static org.junit.Assert.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;



public class jTest {

	@Test
	public void test() {
		chunking CTest = new chunking();
		String ticket = "";
		//Try and get the ticket using the Username and password
		
		// Check to see if the login was OK
		String url = "http://nkxcp21:90/cp-rest";
		ticket = CTest.GetTicket(url,"dmadmin", "demo.demo");
		if (!ticket.isEmpty()) {
			//If the ticket isn't empty then we're going to try and upload a file
			String FilePath = "C:/Users/dmadmin/Desktop/Image0001.tif";
			//Now Upload the file to the server
			String FileID = null;
			FileID = CTest.UploadFile(url,ticket, FilePath);
		}
		fail("Not yet implemented");
	}

}
