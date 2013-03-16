package Main;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.nio.file.Files;

public class Client{
	Socket clientSock;
	ObjectInputStream ois = null;
	ObjectOutputStream oos = null;
	
	public Client(){
		this.clientSock = null;
		this.ois = null;
		this.oos = null;
	}

	public String[] parse(String inString){ //not sure if this is needed - leave for now
//		System.out.println("parsing: "+inString);
		String[] tokens = inString.split(" ");
		
//		System.out.println(this.commandProcessor(tokens[0], tokens));
		while(this.commandProcessor(tokens[0], tokens).equals("error")){
			// need input again
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("drs: input a command");
				try{
					String input = reader.readLine();
					tokens = input.split(" ");
				}catch(IOException e){

				}
				
		}
		// validated at this point
		return tokens; 
	}
	
	public String commandProcessor(String command,String[] files){//checks syntax
		System.out.println("Processing...");
		switch(command){
			case "store": 
				if(files.length < 3 ){//error
					System.out.println("DRS store Usage: store <full path to file> <file name to be used in drs>");
					return "error";
				}

				//file checking here
				File f = null;
				try{
					f = new File(files[1]);
				}catch(Exception e){ }

				if(f.exists()){
					//file exists, valid will be returned
					//System.out.println("File exists");
				}else{
					System.out.println("File does not exist");
					return "error";
				} 

				break;
			case "retrieve": 
				if(files.length < 3 ){//error
					System.out.println("DRS retrieve Usage: retrieve <file name in drs> <file name to be used in local file system>");
					return "error";
				}

				break;
			case "status": 
				//System.out.println("status");		
				break;
			case "exit":
				System.out.println("Client ending");
				System.exit(0);

				break;
			default:
				System.out.println("undefined operation");
				return "error";
		}
		return "valid";
	}

	public void responseHandler(String command, String[] response, String[] request){
//		System.out.println(command + " : "+ response[0]);
		if(command.equals("store")){
			this.sendFile(response,request);
		}else if(command.equals("retrieve")){
			this.retrieveFile(response,request );
		}else if(command.equals("status")){
			printStatus(response);
		}
	}

	public void printStatus(String[] in){
		String[] temp = null;

		System.out.println("=====================================");
		System.out.println("ip                       port   total number of files");
		System.out.println("=====================================");
		//print data here
		for(int i = 0 ; i < in.length; i++){
			temp = in[i].split(",");
			System.out.print(temp[0]);
			System.out.print("    ");
			System.out.print(temp[1]);
			System.out.print("    ");
			System.out.print(temp[2]);
			System.out.println();
			
		}
		System.out.println("=====================================");
	}

	public void sendFile( String[] servers, String[] request ){	
		//client chooses a data server from the list as primary and sends the list and file to it
		// all dataserver socket communications will reside here for this operation
		String[] tmp = servers[0].split(",");

//		System.out.println("entering sendfile");
		
		//1. pick first server from list (primary).
		String servaddr = tmp[0];
		int port = Integer.parseInt(tmp[1]);
		Socket dataServSock = null;
		boolean unconnected = true;
		int index = 1;
		ObjectInputStream dois = null;
		ObjectOutputStream doos  = null;

		while(unconnected){
//			System.out.println("while");
			try{
				dataServSock = new Socket(servaddr,port); 
				doos = new ObjectOutputStream(dataServSock.getOutputStream());
				dois = new ObjectInputStream(dataServSock.getInputStream());
	

			}catch(IOException e){
				System.out.println(servaddr+ " is unresponsive");
				if(index > servers.length-1){
					System.out.println("No Data Servers Available - Exiting");
					System.exit(1);
				}
				tmp = servers[index].split(",");
				servaddr = tmp[0];
				port = Integer.parseInt( tmp[1] );
				index++;
				continue;
			}
			unconnected = false;
//			System.out.println("connected");
		}//end while

		// 2. send server list -  change to request?
		try{

			doos.writeObject(request);
//			System.out.println("retrieve request sent");

			String[] response = (String[]) dois.readObject();
//			System.out.println(response[0]);

			if(response[0].equals("ACK")){
					//all good
			}else{
				System.out.println(response[0]);
			}

			doos.writeObject(servers);
//			System.out.println("serverlist sent");

			response = (String[]) dois.readObject();
//			System.out.println(response[0]);
			
			if(response[0].equals("ACK")){
				//all good
			}else{
				System.out.println(response[0]);
			}
			
		}catch(Exception e ){
			e.printStackTrace();
		}

		//3. 	send file
			File f = null;
			try{
				f = new File(request[1]);
				byte[] content = Files.readAllBytes(f.toPath());
				doos.writeObject(content);


				String[] resp = (String[]) dois.readObject();
//				System.out.println(response[0]);
			

		//4. get confirmation?
				if(resp[0].equals("ACK")){
					//all good
				}else{
					System.out.println(resp[0]);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		System.out.println("File "+request[1]+" has been stored as "+ request[2]+".");

		try{dataServSock.close();}
		catch(Exception e){}
	}

	public void retrieveFile(String[] servers, String[] request ){	
		// client chooses a data server from the list and requests the file from it
		// all dataserver socket communications will reside here for this operation

		//check for this error in location 0 of servers: "filedoesnotexist"
		if(servers[0].equals("filedoesnotexist")){//file not in drs
			System.out.println("The specified file is not in DRS");
		}else{
			String[] tmp = servers[0].split(",");

			//1. pick first server from list.
			String servaddr = tmp[0];
			int port = Integer.parseInt(tmp[1]);
			Socket dataServSock = null;
			boolean unconnected = true;
			int index = 1;
			ObjectInputStream dois = null;
			ObjectOutputStream doos  = null;

			while(unconnected){
				try{
					dataServSock = new Socket(servaddr,port); 
					doos = new ObjectOutputStream(dataServSock.getOutputStream());
					dois = new ObjectInputStream(dataServSock.getInputStream());

				}catch(IOException e){
					System.out.println( servaddr + " is unresponsive" );
					if(index > servers.length-1){
						System.out.println("No Data Servers Available - Exiting");
						System.exit(1);
					}
					tmp = servers[index].split(",");
					servaddr = tmp[0];
					port = Integer.parseInt( tmp[1] );
					index++;
					continue;
				}
				unconnected = false;
//				System.out.println("connected");
			}//end while

			//at this point am connected to a data server that has the file
			// 2. send request
			try{
				doos.writeObject(request);
//				System.out.println("retrieve request sent");

				String[] response = (String[]) dois.readObject();
//				System.out.println(response[0]);

				if(response[0].equals("ACK")){
					//all good
				}else{
					System.out.println(response[0]);
				}


				//3. receive file
				byte[] content = (byte[]) dois.readObject();

				String[] ack = {"ACK"};
			   	doos.writeObject(ack);

				//4. store file in usr specified path 
				File f  = new File(request[2]);
				Files.write(f.toPath(), content);


			}catch(Exception e ){
				e.printStackTrace();
			}

			System.out.println("File "+request[1]+" has been retrieved as "+ request[2]+".");

			try{dataServSock.close();}
			catch(Exception e){}
		}//end else
	}



	public boolean connectToServer(String host, int port){
		boolean status = true;

		try {
			this.clientSock = new Socket(host,port);		
			this.oos = new ObjectOutputStream(this.clientSock.getOutputStream());
			this.ois = new ObjectInputStream(this.clientSock.getInputStream());			
			
		} catch (UnknownHostException e) {
			status = false;	
			e.printStackTrace();
		} catch (IOException e) {
			status = false;	
			e.printStackTrace();
		}

		return status;
	}

	public static void main(String[] args) {
		System.out.println("Client starting");

		Client client = new Client();

		if(args.length < 2){
			System.out.println("Usage: java Client <metadata-host> <metadata-port>");
			System.exit(0);
		}

		//should connect to meta server here
		boolean success  = client.connectToServer(  args[0], Integer.parseInt(args[1])  );

		boolean flag = success;
		boolean acceptInput = true;
		//String command = null;//set so that this happens the first time
		String[] tokens = null;
		String[] response = null;
		while(flag){

			if(acceptInput){
				String input=null;
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("drs: input a command");
				try{
					input = reader.readLine();
				}catch(IOException e){

				}
//				System.out.println("command entered: "+ input);
				 tokens = client.parse(input);

				//if a meta/data server request was required, it would be set here
				//command = tokens[0];
			}



			try{
				client.oos.writeObject(tokens);
				response = (String[]) client.ois.readObject();
				client.responseHandler(tokens[0],response, tokens);
			}catch (Exception e) {
				e.printStackTrace();
			}


			//flag = false;// for breaking while loop

		}



		System.out.println("Client ending");

	}

}
