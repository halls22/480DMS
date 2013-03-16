package Main;

class FileObject{
	private int timestamp;
	private int size;
	private String filename;

	public FileObject(String filename, int size){//needs args
		// TODO: initialize timestamp to current time here
		this.timestamp = 0; 
		this.size = size; 
		this.filename = filename; 
	}

	public int getTimestamp(){
		return this.timestamp; // returns timestamp of this file
	}

	public String getName(){
		return this.filename;
	}
	
	public int getSize(){
		return this.size ; 
	}
}