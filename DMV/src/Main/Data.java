package Main;

// make serializable?
// add directory methods
// change name to ServData?

import java.util.ArrayList;

class Data{
	String serverName;
	int port;
	int load;
	//	ArrayList<String> files; 
	ArrayList<FileObject> files;//dirs?

	public Data(String serverName, int port){
		this.serverName = serverName;
		this.port = port;
		this.load = 0;
		this.files = new ArrayList<FileObject>();
	}

	public void addFile(String fileName){//need additional fields here. 
		this.load++;
		FileObject file = new FileObject(fileName,0);// fix this
		this.files.add(file);
	}
	
	public ArrayList<FileObject> getFilenames(){// change, or make individual methods for returning diff metadata
		ArrayList<FileObject> temp = this.files;
		return temp;
	}	

}//end class
