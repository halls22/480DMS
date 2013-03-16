package Main;

/*Andrew Hopkins
 *CS480
 *PA1
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MetaDataServer{
	final int degree; // Degree of replication
	private MetaData info; //Object for storing MetaData
	ServerSocket serversock;
	int numberOfServers;
	
	public MetaDataServer(int port, int degreeOfReplication){ //Constructer
		this.degree = degreeOfReplication;
		this.info = new MetaData();
		this.numberOfServers = 0;

		try{
			this.serversock = new ServerSocket( port );
		}catch(IOException e){
			System.out.println("Socket Creation failed, try a different port");
			System.exit(0);
		}
	}

	public void loadServers(String fileName){
		Scanner scanner = null;
		String temp = null;
		String host = null;
		String port = null;
		String[] tmparray = new String[2];

		try{
			File file = new File(fileName);
			scanner = new Scanner(file);
		}catch(FileNotFoundException e){
			System.out.println("configuration file does not exist");
			System.exit(0);
		}

		while(scanner.hasNextLine()){
			temp = scanner.nextLine();
//			System.out.println(temp);
			tmparray = temp.split(",");
			this.info.addServer(tmparray[0], Integer.parseInt( tmparray[1]));
			this.numberOfServers++;
		}
	
		System.out.println("configured");
		scanner.close();
	}

	public String[] selectServers(String fileName){//for store
		String[] outList = new String[this.degree];
		String temp = null;
		int deg = this.degree;
		int pos = -1;
		int min;
		int index = 0;

		//check to see if this is an update
		String[] updateList = retrieve(fileName);
		if(updateList[0].equals("filedoesnotexist")){
			// carry on with process below
		}else{//file already in drs, return current locations
			return updateList;
		}


		// for new files
		while(deg > 0){

			min = 1000;
			for(int i = 0; i < this.info.serverList.size(); i++ ){
				if(this.info.serverList.get(i).load < min){
					pos = i; 
					min = this.info.serverList.get(i).load;
				}
			}//end for

//			outList.add(this.info.serverList.get(pos));
			temp = this.info.serverList.get(pos).serverName
                    + "," + this.info.serverList.get(pos).port;
			outList[index] = temp;
			this.info.addFile(this.info.serverList.get(pos).serverName, fileName);
			deg--;
			index++;

		}//end while

		return outList; 
	}



	public String[] retrieve(String fileName){// for retrieve
		String[] outList = new String[this.degree];
		ArrayList<String> list = this.info.getFileList(); 
		boolean exists = false;

		// first: check to see if the file is in system, throw error if not
		for (int i = 0; i< list.size(); i++){
			if(list.get(i).equals(fileName)){
				//file exists
				exists = true;
			}
		}

		if(exists){
			//file exists	
		}else{
			//file doesnt exist in drs
			outList[0] = "filedoesnotexist";
			return outList;
		}

		//second: get list of servers holding this file
		list = info.getStorageLocations(fileName);
		list.toArray(outList);
/*		for (int i = 0; i< outList.length; i++){
			System.out.println(outList[i]);
		}
*/
		return outList; 
	}





	public String[] getStatus(){ // for status
		String[] outList = new String[this.numberOfServers];
		String temp = null;//format: name,port,#of files
		Data tmp = null;
		
		for(int i = 0; i< this.info.serverList.size();i++ ){
			tmp = this.info.serverList.get(i);
			temp = tmp.serverName +","+
				   tmp.port+","+
				   tmp.load;
			outList[i] = temp;
		}

		return outList;
	}





	public String[] handleRequest(String[] request){
		String[] out = null;


//		System.out.println(request[0]);
		if(request[0].equals("store")){
//			System.out.println("command is store");	
			out = selectServers(request[2]);
		}else if(request[0].equals("retrieve")){
//			System.out.println("command is retrieve");
			out = retrieve(request[1]);
		}else if(request[0].equals("status")){
//			System.out.println("command is status");
			out = getStatus();
		}
		
//		out = new String[1];
//		out[0] = "ack";
		return out;
	}


	public static void main(String[] args) {
		System.out.println("Meta Data Server starting");

		if(args.length < 2){
			System.out.println("Usage: java MetaDataServer <port> <degree of replication>");
			System.exit(0);
		}

		MetaDataServer metaServ = new MetaDataServer( Integer.parseInt( args[0]), Integer.parseInt( args[1]));

		metaServ.loadServers("config.txt");

		Socket clientsock = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos  = null;
		boolean connected = false;
		

		while(true){
			try {
				clientsock = metaServ.serversock.accept();

				ois = new ObjectInputStream(clientsock.getInputStream());
				oos = new ObjectOutputStream(clientsock.getOutputStream());

				System.out.println("client connected");
				connected = true;

			} catch (IOException e) {	
				connected = false;
				//e.printStackTrace();
				System.out.println("client error");
				break;	
			}
			

			//begin communication 
			while(connected){
				try{
					String[] request = (String[])ois.readObject();
//					System.out.println(request[0]);

					String[] response = metaServ.handleRequest(request);

					oos.writeObject(response);


				}catch (Exception e) {
					//e.printStackTrace();
					System.out.println("client disconnected");
					connected = false;
					//System.exit(1);
				}
			}//end communications loop
		
		}//end accept while


		try{metaServ.serversock.close();}
		catch(IOException e){}
		System.out.println("Meta Data Server ending");
		System.exit(0);
	}

}
