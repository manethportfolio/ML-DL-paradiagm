import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{
	public static void main(String argv[]) throws Exception
	{
		// Set the port number.
		int port = 6789;

		 String rootDirectory = "."; // Root directory to serve files from

	        try {
	            ServerSocket serverSocket = new ServerSocket(port);
	            System.out.println("Web server is listening on port " + port);

	            while (true) {
	                Socket clientSocket = serverSocket.accept();
	                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

	                // Read input from client
	                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	                String request = in.readLine();
	                System.out.println("Request: " + request);

	                // Extract file path from GET request
	                String filePath = rootDirectory + request.split(" ")[1];

	                // Open requested file
	                FileInputStream fileInputStream;
	                try {
	                    fileInputStream = new FileInputStream(filePath);
	                    byte[] fileData = new byte[fileInputStream.available()];
	                    fileInputStream.read(fileData);
	                    fileInputStream.close();

	                    // Send HTTP response headers
	                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
	                    out.writeBytes("HTTP/1.1 200 OK\r\n");
	                    out.writeBytes("Content-Type: text/html\r\n");
	                    out.writeBytes("Content-Length: " + fileData.length + "\r\n");
	                    out.writeBytes("\r\n");

	                    // Send file data
	                    out.write(fileData);
	                    out.flush();
	                } catch (FileNotFoundException e) {
	                    // If file not found, send 404 response
	                    String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
	                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
	                    out.writeBytes(notFoundResponse);
	                    out.flush();
	                }

	                // Close client socket
	                clientSocket.close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}

final class HttpRequest implements Runnable
{
	final static String CRLF = "\r\n";
	private static final int PORT = 8080;
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception 
	{
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run()
	{
		   try {
	            // Process the HTTP request message
	            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

	            // Read the request message from the client
	            String requestLine = in.readLine();
	            System.out.println("Request: " + requestLine);
	            
	            // Extract the filename from the request line
	            StringTokenizer tokens = new StringTokenizer(requestLine);
	            tokens.nextToken(); // skip over the method, which should be "GET"
	            String fileName = tokens.nextToken();
	            // Prepend a "." so that file request is within the current directory.
	            fileName = "." + fileName;
	            
	            // Open the requested file
	            FileInputStream fis = null;
	            boolean fileExists = true;
	            try {
	                fis = new FileInputStream(fileName);
	            } catch (FileNotFoundException e) {
	                fileExists = false;
	            }
	            
	            // Construct the response message
	            String statusLine = null;
	            String contentTypeLine = null;
	            String entityBody = null;
	            File file = new File("Spacex.jpg"); // Load the image file
	            if (file.exists()) {
	                fis = new FileInputStream(file);
	                statusLine = "HTTP/1.1 200 OK" + CRLF;
	                contentTypeLine = "Content-type: " + contentType("Spacex.jpg") + CRLF;
	                entityBody = "";
	            } else {
	                statusLine = "HTTP/1.1 404 Not Found" + CRLF;
	                contentTypeLine = "Content-type: text/html" + CRLF;
	                entityBody = "<HTML>" +
	                             "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
	                             "<BODY>Not Found</BODY></HTML>";
	            }

	            
	            // Send the status line
	            out.writeBytes(statusLine);
	            
	            // Send the content type line
	            out.writeBytes(contentTypeLine);
	            
	            // Send a blank line to indicate the end of the header lines
	            out.writeBytes("\r\n");
	            
	            // Send the entity body
	            if (fileExists) {
	                sendBytes(fis, out);
	                fis.close();
	            } else {
	                out.writeBytes(entityBody);
	            }
	            
	            // Close the streams and socket
	            out.close();
	            in.close();
	            socket.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	            
	            
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket
        byte[] buffer = new byte[1024];
        int bytes = 0;
        
        // Copy requested file into the socket's output stream
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
	
	private static String contentType(String fileName) {
        // Determine the MIME type based on the file extension
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream";
        }
    }
	            
	            
	            
	           

	@SuppressWarnings("unused")
	private void processRequest() throws Exception {
		// Get a reference to the socket's input and output streams.
	    InputStream is = socket.getInputStream();
	    DataOutputStream os = new DataOutputStream(socket.getOutputStream());
	    
	    // Set up input stream filters.
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    
	    // Get the request line of the HTTP request message.
	    String requestLine = br.readLine();
	    
	    // Display the request line.
	    System.out.println();
	    System.out.println(requestLine);
	    
	    // Get and display the header lines.
	    String headerLine = null;
	    while ((headerLine = br.readLine()).length() != 0) {
	        System.out.println(headerLine);
	    }
	    
	    // Close streams and socket.
	    os.close();
	    br.close();
	    socket.close();
	}
}

