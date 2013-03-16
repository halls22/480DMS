package Main;

// object to store info for meta server
import java.util.ArrayList;

public class MetaData{
	ArrayList<Data> serverList; 
	ArrayList<String> fileList; 

	public MetaData(){
		this.serverList = new ArrayList<Data>();
		this.fileList = new ArrayList<String>();
	}

	public void addServer(String serverName, int port){
//		System.out.println("Server "+ serverName+", port "+port+" added.");
		Data tmp = new Data(serverName,port);
		this.serverList.add(tmp);
	}

	public void addFile(String serverName, String fileName){
		this.fileList.add(fileName);
		for(int i = 0 ; i < this.serverList.size(); i++){
//			System.out.println(serverList.get(i).serverName);
			if(serverList.get(i).serverName.equals(serverName)){
				serverList.get(i).addFile(fileName);
			}
		}//end for
	}//end method

	public ArrayList<String> getStorageLocations(String fileName){
		ArrayList<String> locations = new ArrayList<String>();
		String temp = null;
		Data tmp; 
		 
		for(int i = 0 ; i < this.serverList.size(); i++){
		 	tmp = serverList.get(i);
		 	
		 	for(int j = 0; j < tmp.files.size(); j++){
		 		if(tmp.files.get(j).equals(fileName)){
					temp = tmp.serverName+","+tmp.port;
		 			locations.add(temp);
		 		}//end if
		 	}//end inner for
		}//end outer for
		 
		return locations; // hostnames
	}

	public ArrayList<String> getFileList(){
		ArrayList<String> tmplst = this.fileList;
		return tmplst; 
	}

	// way to return metadata
	// sort by timestamp
	// adding nessesary fields to metadata: filesize, time stamp, 
	// directories

	// use best practice private fields
	
	
	// have metadata stored centrally or in each servers meta data? or both? 


}//end class
