import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

// This class represents a simple rectangular image, where each pixel can be
// one of 16 colours.
public class Image {

  // Store a 2 dimensional image with "colours" as numbers between 0 and 15
  private int pixels[][];

  // Read in an image from a file. Each line of the file must be the same
  // length, and only contain single digit hex numbers 0-9 and a-f.
  public Image(String filename) {

    // Read the whole file into lines
    ArrayList<String> lines = new ArrayList<String>();
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      for (String s = in.readLine(); s != null; s = in.readLine())
        lines.add(s);
    }
    catch (FileNotFoundException e) {
      System.out.println("File not found: " + filename);
      System.exit(1);
    }
    catch (IOException e) {
      System.exit(2);
    }

    if (lines.size() == 0) {
      System.out.println("Empty file: " + filename);
      System.exit(1);
    }

    // Initialise the array based on the number of lines and the length of the
    // first one.
    int length = lines.get(0).length();
    pixels = new int[lines.size()][length];

    for (int i = 0; i < lines.size(); i++) {
      // Check that all of the lines have the same length as the first one.
      if (length != lines.get(i).length()) {
        System.out.println("Inconsistent line lengths: " + length + " and " + lines.get(i).length() + " on lines 1 and " + (i+1));
        System.exit(1);
      }

      // Copy each line into the array
      for (int j = 0; j < length; j++) {
        pixels[i][j] = Character.getNumericValue(lines.get(i).charAt(j));
        if (pixels[i][j] < 0 || pixels[i][j] > 15) {
          System.out.println("Invalid contents: " + lines.get(i).charAt(j) + " on line " + (i+1));
          System.exit(1);
        }
      }
    }
  }

  // Create a solid image with given dimensions and colour
  public Image(int height, int width, int colour) {
    pixels = new int[height][width];
    for (int i = 0; i < height; i++)
      for (int j = 0; j < width; j++)
        pixels[i][j] = colour;
  }

  // Get back the original text-based representation
  public String toString() {
    StringBuilder s = new StringBuilder(pixels.length * pixels[0].length);
    for (int i = 0; i < pixels.length; i++) {
      for (int j = 0; j < pixels[i].length; j++)
        s.append(Integer.toHexString(pixels[i][j]));
      s.append("\n");
    }
    return s.toString();
  }

  public void setPixels(int y, int x, int colour){
      pixels[y][x] = colour;
  }

  // TASK 2: Implement the compress method to create and return a list of
  // drawing commands that will draw this image.
  // 6 marks for correctness -- does the command list exactly produce the
  // input image.
  // 5 marks for being able to shrink test-image1 and test-image2 into no more
  // than 20 and 35 commands respectively. You can work out these commands by
  // hand, but the marks here are for your implemented algorithm (HINT: think
  // Run-length Encoding) being able to create the commands.
  // 4 marks for shrinking the other, more difficult, test images. We'll run
  // this as a competition and give all 4 to the best 20% of the class, 3 to
  // the next best 20%, and so on.

// Works fine for test-image1, nearly fine for test-image2 and 3 but not the others.
  public Drawing horizontalScan(){
  	Drawing horizontalScan = new Drawing(pixels.length, pixels[0].length, pixels[0][0]);
  	int x = 0;
  	int y = 0;
  	// Horizontal Scanning
  	while(y < pixels.length){

  		while(x < pixels[y].length-1){
  			int current = pixels[y][x];
  			int runLength = 0;
  			while(x < pixels[y].length-1 && pixels[y][x] == current){
  				x++;
  				runLength++;
  			}
  			horizontalScan.addCommand(new DrawingCommand("right " + runLength + " " + Integer.toHexString(current)));
  		}

  		y++;
  		x = pixels[y].length-1;
  		horizontalScan.addCommand(new DrawingCommand("down 1 " + Integer.toHexString(pixels[y][x])));
  		while(x > 0){
  			int current = pixels[y][x];
  			int runLength = 0;
  			while(x > 0 && pixels[y][x] == current){
  				runLength++;
  				x--;
  			}
  			horizontalScan.addCommand(new DrawingCommand("left " + runLength + " " + Integer.toHexString(current)));
  		}
  		y++;
  		if(y < pixels.length-1)
  			horizontalScan.addCommand(new DrawingCommand("down 1 " + Integer.toHexString(pixels[y][x])));
  	}
  	return horizontalScan;
  }

//Works fine for test image 2 but not so much the others.
  public Drawing verticalScan(){
  	//Vertical Scanning
  	Drawing verticalScan = new Drawing(pixels.length, pixels[0].length, pixels[0][0]);
  	int x = 0;
  	int y = 0;
  	
  	while(x < pixels[y].length-1){
  		if(pixels[y+1][x] == verticalScan.getBackground()){
  			x++;
  			verticalScan.addCommand(new DrawingCommand("right 1 " + Integer.toHexString(pixels[y][x])));
      }
  		while(y < pixels.length-1){
  			int current = pixels[y][x];
  			int runLength = 0;
  			while(y < pixels.length-1 && pixels[y][x] == current){
  				y++;
  				runLength++;
  			}
  			verticalScan.addCommand(new DrawingCommand("down " + runLength + " " + Integer.toHexString(current)));
  		}
    
  
  		if(pixels[y][x+1] == verticalScan.getBackground()){
  			x++;
  			verticalScan.addCommand(new DrawingCommand("right 1 " + Integer.toHexString(pixels[y][x])));
  		}

  		x++;
  		y = pixels.length-1;
  		verticalScan.addCommand(new DrawingCommand("right 1 " + Integer.toHexString(pixels[y][x])));
  		while(y > 0){
  			int current = pixels[y][x];
  			int runLength = 0;
  			while(y > 0 && pixels[y][x] == current){
  				runLength++;
  				y--;
  			}
  			verticalScan.addCommand(new DrawingCommand("up " + runLength + " " + Integer.toHexString(current)));
  		}

  		x++;
  		if(x < pixels[0].length-1)
  			verticalScan.addCommand(new DrawingCommand("right 1 " + Integer.toHexString(pixels[y][x])));
  	}
  	return verticalScan;
  }
 

// Gets test-image 1 down to 19 commands and test-image2 down to 29. 
// Doesn't work as well with the other test files.
  public Drawing compress(){

  	//Checks which algorithm produces the least commands 
  	//for the image and returns it.
  	
  	if(horizontalScan().amountOfCommands() <= verticalScan().amountOfCommands()){
  		return horizontalScan();
  	}else{
  		return verticalScan();
  	}
  }



  // This is the standard 4-bit EGA colour scheme, where the numbers represent
  // 24-bit RGB colours.
  static int[] colours =
  {0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
   0xAA0000, 0xAA00AA, 0xAA5500, 0xAAAAAA,
   0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
   0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF};

  // Render the image into a PNG with the given filename.
  public void toPNG(String filename) {

    BufferedImage im = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < pixels.length; i++)
      for (int j = 0; j < pixels[i].length; j++) {
        im.setRGB(j,i,colours[pixels[i][j]]);
      }

    File f = new File(filename + ".png");
    try {
      ImageIO.write(im,"PNG",f);
    }
    catch (IOException e) {
        System.out.println("Unable to write image");
        System.exit(1);
    }
  }

  public static void main(String[] args) throws BadCommand{
    // A simple test to read in an image and print it out.
    Image i = new Image(args[0]);
    Drawing txtFile = i.compress();
    System.out.println(txtFile.toString());
    System.out.println(txtFile.amountOfCommands());
    System.out.print(txtFile.draw().toString());
  }
}
