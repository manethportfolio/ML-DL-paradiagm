import java.io.*;
import java.net.*;

class TCPServer {
	
	public static void main(String argv[]) throws Exception
	{
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (true) 
		{
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient =
				new BufferedReader (new InputStreamReader(
					connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream (
				connectionSocket.getOutputStream());
			while (true) 
			{
				clientSentence = inFromClient.readLine();
				capitalizedSentence = 
				clientSentence.toUpperCase() + '\n';
				outToClient.writeBytes(capitalizedSentence);
				
				// Check for image file request
				if (clientSentence.endsWith(".jpg")) {
					System.out.println("Client requested image file: " + clientSentence);
					FileInputStream fileInputStream = null;
					boolean fileFound = true;
					try {
						fileInputStream = new FileInputStream(clientSentence);
					} catch (FileNotFoundException e) {
						fileFound = false;
					}
					if (fileFound) {
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						byte[] buffer = new byte[4096]; // or any other appropriate buffer size
						int bytesRead;
						while ((bytesRead = fileInputStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, bytesRead);
						}
						byte[] fileData = outputStream.toByteArray();
						outToClient.write(fileData, 0, fileData.length);
						outToClient.flush();
						System.out.println("Image file sent to client.");
						fileInputStream.close();
					} else {
						outToClient.writeBytes("Status for file not found\n");
						System.out.println("Image file not found.");
					}
				} else {
					// Process other requests
					capitalizedSentence = clientSentence.toUpperCase() + '\n';
					outToClient.writeBytes(capitalizedSentence);
				}
				connectionSocket.close();
			}
		}
	}
}
