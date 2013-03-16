package Vis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import Extra.DebugPrints;

public class FileVisual extends DebugPrints{

	/////////////// CLASS VARIABLES
	
	private String path; 											 // current directory 
	private ArrayList<Integer> fileSizes = new ArrayList<Integer>(); // size of directory files in bytes
	private PixelImageCreator pi; 									 // generates pixel visuals

	
	/////////////// CLASS METHODS
	
	public FileVisual(String path){
		this.path = path;
		initialize();
	}

	
	/**
	 * analyses the directory given in the global variable path
	 * and creates a new integer arraylist of the file sizes
	 * stores it in class variable "filesizes"
	 */
	private void initialize(){
		// open the path, make sure it's a directory
		File directory = null;
		try{
			directory = new File(path);
		} catch(Exception e) {
			System.err.println("Error: " + e);
		}
		if(directory == null || !directory.isDirectory()){
			printAndExit("unable to initialize \"" + path + "\", not a directory", 2);
		}

		// get a list of the file sizes (in bytes), add them to arraylist
		File[] files = directory.listFiles();
		addSizes(files);
		debug("Successfully initialized directory \"" + directory +"\" with " + fileSizes.size() + " files.");
	}

	
	
	/**
	 * takes an array of files and adds their sizes to class
	 * variable "filesizes"
	 * @param files array 
	 */
	private void addSizes(File[] files) {
		for(File f : files){
			debug("file: " + f.getName() + " (" + f.length() + " bytes)");
			fileSizes.add((int) f.length());
		}
	}
	
	/**
	 * change the path that the FileVisual is configured for.
	 * will erase all stored data about current directory,
	 * and gather new information about given path.
	 * @param path
	 */
	public void changePath(String path){
		this.path = path;
		initialize();
	}

	/**
	 * takes the name of a directory and creates an image
	 * showing the filesizes in that directory. returns the path of the image
	 * 
	 * @param directory String of the directory holding the files
	 * @return path String of the created image
	 * @throws IOException 
	 */
	public String createPixelImage() throws IOException{	
		pi = new PixelImageCreator(toIntArray(fileSizes)); // send this off to the image creator
		return pi.generateImage(); 
	}

	/**
	 * takes an arraylist and returns the corresponding int array
	 * @param list arraylist you want to convertt
	 * @return int[]
	 */
	int[] toIntArray(ArrayList<Integer> list){
		int[] ret = new int[list.size()];
		for(int i = 0;i < ret.length;i++)
			ret[i] = list.get(i);
		return ret;
	}

	
	
	
	/**
	 * START HERE. Example of how to use it
	 * comment out later
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FileVisual vis = new FileVisual("E:\\"); // select the directory you want a visual of
		System.out.println("output: " + vis.createPixelImage());
	}

}
