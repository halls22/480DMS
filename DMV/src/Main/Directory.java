package Main;

import java.util.ArrayList;

class Directory{
	private int timestamp;
	private int size;
	private String name;
	private ArrayList<Object> datalist;

	public Directory(int size, String name){
		//may need multiple constructors
		// TODO: initialize timestamp to current time here
		this.timestamp = 0;
		this.size = size;
		this.name = name;
		this.datalist = new ArrayList<Object>();
	}
	
	public int getTimestamp(){
		return this.timestamp; 
	}
	
	public int getSize(){
		return this.size; 
	}
	
	public String getDirName(){
		return this.name; 
	}

	public void addFile(FileObject file){
	
	}
	
	public void addDirectory(Directory dir){
	
	}
	
	

}