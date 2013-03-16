package Main;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.nio.file.Files;

public class DataServer{
	String path;
	int numberOfFiles; //shouldnt have to store this here also, but it will make things easier
	ServerSocket serversock;
	String[] request;
	String[] replicationList;
	Socket clientsock;
	ObjectInputStream ois;
	ObjectOutputStream oos;
 
	public DataServer(String storagePath, int port){
		this.path = storagePath;
		this.numberOfFiles = 0;
		this.request = null;
		this.replicationList = null ;
		this.clientsock = null;
		this.ois = null;
		this.oos  = null;
		
		File  f = new File(storagePath);
		f.mkdirs();

		try{
			this.serversock = new ServerSocket( port );
		}catch(IOException e){
			System.out.println("port already in use, please try another");
			System.exit(0);
		}
	}

	public void replicateToServers(byte[] content){
//		System.out.println("hello from replicateToServers");
		int port;
		String ip = null;
		String[] newServList = null;
		ObjectInputStream localIS = null;
		ObjectOutputStream localOS = null;
		String localhostname = null;
		int index=0;

		//1. parse server list, remove this server, get next server port and name
		newServList = new String[this.replicationList.length - 1];
		try{
			localhostname = java.net.InetAddress.getLocalHost().getHostName();
//			System.out.println(localhostname);
		}catch(Exception e){}
	
		for(int i = 0; i<this.replicationList.length;i++){
			if(this.replicationList[i].contains(localhostname)){
				//dont add to new list
			}else{
				newServList[index] = this.replicationList[i];
				index++;
			}
		}
		
		if(newServList.length == 0){
			return;
		}

		String[] nextServ = newServList[0].split(",");
		ip = nextServ[0];
		port = Integer.parseInt(nextServ[1]);

		try{
			//2 connect to next server
			Socket sock = new Socket(ip,port);	
			localOS = new ObjectOutputStream(sock.getOutputStream());		
			localIS = new ObjectInputStream(sock.getInputStream());

			//3. transfer request/file in a way that will trigger store file
//			System.out.println("beginning transfer");
//begin Client copy
			localOS.writeObject(this.request);
//			System.out.println("retrieve request sent");

			String[] response = (String[]) localIS.readObject();
//			System.out.println(response[0]);

			if(response[0].equals("ACK")){
					//all good
			}else{
				System.out.println(response[0]);
			}

			localOS.writeObject(newServList);
//			System.out.println("serverlist sent");

			response = (String[])localIS.readObject();
//			System.out.println(response[0]);
			
			if(response[0].equals("ACK")){
				//all good
			}else{
				System.out.println(response[0]);
			}

			localOS.writeObject(content);


			String[] resp = (String[]) localIS.readObject();
//			System.out.println(response[0]);
			if(resp[0].equals("ACK")){
				//all good
			}else{
				System.out.println(resp[0]);
			}
//end copy
		    //4. close sock
			sock.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void storeFile(){//stores the file to be replicated in tmp
		this.numberOfFiles = this.numberOfFiles + 1;// increase number of files stored here		
		try{
			String[] ack = {"ACK"};
			this.oos.writeObject(ack);

			String[] replication = (String[]) this.ois.readObject();
			this.oos.writeObject(ack);
			this.replicationList = replication;
			
			File f  = new File(this.path+this.request[2]);
			byte[] content = (byte[]) this.ois.readObject();
			Files.write(f.toPath(), content);

			this.oos.writeObject(ack);

			//now to replicate
			replicateToServers(content);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void retrieveFile(){// retrieves a file by name - in member var
		//should actually return a file
		//decrease number of files? NO


		System.out.println("hello from retrieve file");

		try{
			String[] ack = {"ACK"};
			this.oos.writeObject(ack);

			File f  = new File(this.path+this.request[1]);
			byte[] content = Files.readAllBytes(f.toPath());
			this.oos.writeObject(content);

			ack = (String[]) this.ois.readObject();

			if(ack[0].equals("ACK")){
				//all good
			}else{
				System.out.println(ack[0]);
			}


		}catch(Exception e){
			e.printStackTrace();
		}


	}//end retrieveFile


	public void handleRequest(String[] request){
		this.request = request;
		if(request[0].equals("retrieve")){// retrieve request
			this.retrieveFile();
		}else{// store request
			this.storeFile();
		}
//		String[] ack = {"ACK"};
//		return ack;
	}



	public static void main(String[] args) {
		System.out.println("Data Server starting");

		if(args.length < 1){
			System.out.println("Usage: java DataServer <port>");
			System.exit(0);
		}
			
		DataServer storage = new DataServer("/tmp/hopkinsa/DRS/", Integer.parseInt( args[0]));

		boolean connected = false;

		
		while(true){

			try {
				storage.clientsock = storage.serversock.accept();
				storage.ois = new ObjectInputStream(storage.clientsock.getInputStream());
				storage.oos = new ObjectOutputStream(storage.clientsock.getOutputStream());

				System.out.println("client connected");
				connected = true;

			} catch (IOException e) {	
				connected = false;
				e.printStackTrace();
				System.out.println("client error");
				break;	
			}
			


			//begin communication 
			while(connected){
				try{
					String[] request = (String[]) storage.ois.readObject();
//					System.out.println(request[0]);

					storage.handleRequest(request);

					//storage.oos.writeObject(response);


				}catch (Exception e) {
					//e.printStackTrace();
					System.out.println("client disconnected");
					connected = false;
					//System.exit(1);
				}
			}//end communications loop


		
		}//end while



		try {storage.serversock.close();} 
		catch (IOException e) { }
		System.out.println("Data Server ending");
	}//end main
}
