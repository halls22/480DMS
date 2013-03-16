package Vis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import Extra.DebugPrints;
/**
 * This takes an array of integers and converts them into an image 
 * where every pixel represents a file, and each pixel color
 * represents a filesize.
 * 
 * @param: int[] filesizes
 */
public class PixelImageCreator extends DebugPrints {

	/////////////// CLASS VARIABLES

	private int[] filesizes; // a directories files in bytes
	private int scale = 0; // not implemented yet, but will scale the image up so that you can see the squares

	/////////////// CLASS METHODS

	public PixelImageCreator(){
		filesizes = null;
	}

	public PixelImageCreator(int[] filesizes){
		this.filesizes = filesizes;
		Arrays.sort(this.filesizes);
	}

	public PixelImageCreator(int[] filesizes, int scale){
		this.filesizes = filesizes;
		this.scale = scale;
		Arrays.sort(this.filesizes);
	}

	public void setFilesizes(int[] filesizes){
		this.filesizes = filesizes;
	}

	/**
	 * creates an image where every pixel represents a file.
	 * the different colors show the absolute sizes of the file.
	 * A pure black pixel means the file is <= 1 byte (also used for overflow to make image square).
	 * A pure red pixel means that the file is >= 16 MB.
	 * the isn't good for files bigger than 16 MB because every file 
	 * will show up as red. 
	 * 
	 *@return: String path = path of the image file created
	 * @throws IOException 
	 */
	public String generateImage() throws IOException{
		if(filesizes == null)
			printAndExit("filesizes was not set", 3);
		debug(filesizes); // check to see we are reading some valid filesizes

		//massageFilesizes(); still working on this commented part
		//int[] tmp = {0b111111111111111111111111, 0b11111111111111111111111, 0b1111111111111111111111, 0b111111111111111111111, 0b11111111111111111111, 0b1111111111111111111, 0b111111111111111111, 0b11111111111111111, 0b1111111111111111, 0b111111111111111, 0b11111111111111, 0b1111111111111, 0b111111111111, 0b11111111111, 0b1111111111, 0b111111111, 0b11111111, 0b1111111, 0b111111, 0b11111, 0b1111, 0b111, 0b11, 0b1 };
		//filesizes = tmp;
		
		// determine the dimensions of the image. 100 files should produce a 10x10 image
		int imageWidth = (int)Math.ceil(Math.sqrt(filesizes.length)); // find smallest whole number whose square is big enough to contain all files
		debug("width of the square array: " + imageWidth + ", number of files: " + filesizes.length);

		// create the image in memory
		BufferedImage bi2 = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_RGB);

		// give each pixel in the image a value from the filesizes array
		for(int i=0; i<bi2.getHeight(); i++){
			for(int j=0; j<bi2.getWidth(); j++){
				bi2.setRGB(j, i, filesizeToRGB(j, i, imageWidth));
			}
		}

		// write the file out to disk
		File file = new File("output.png");
		ImageIO.write(bi2, "png", file);
		return file.getAbsolutePath();
	}


	/**
	 * helper method that returns an element from the filesizes array
	 * as a color that can be printed. 
	 * @return color that a pixel should be, given a size in bytes
	 */
	private int filesizeToRGB(int j, int i, int imageWidth){
		if(j+i*imageWidth < filesizes.length){ // if not out of bounds
				return filesizes[j+i*imageWidth];
		} else { // if you are out of bounds, this must be an extra pixel with no associated file
			return 0b00000000111111110000000000000000;
		}
	}
	
	/**
	 * convert an integer like this: 1000101010 to 1111111111
	 * and an integer like:          0000000101 to 0000000111
	 * or                            0000010000 to 0000011111
	 * Basically, flips all bits after the most significant bit
	 * this ensures that colors increase in intensity properly
	 * and doesn't climb up and down the RGB colors
	 */
	private void massageFilesizes(){
		for(int i=0; i<filesizes.length; i++){
			if(filesizes[i] < 0) // if the file doesn't exist (filesize is zero)
				filesizes[i] = Color.black.getRGB();
			else if(filesizes[i] > 0b00000000100000000000000000000000) // if the color is bigger than red
				filesizes[i] = 0b00000000111111111111111111111111; // just make it red
			else if(filesizes[i] >= 0b00000000000000010000000000000000){ // if the color is green or above
				filesizes[i] = filesizes[i] | 0b00000000000000001111111111111111; // set all of the blue pixels to filled
			}
		}
	}
}
