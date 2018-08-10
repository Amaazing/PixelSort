package butt;

import processing.core.*;
import java.util.Arrays;

//import java.lang.Object;

public class PixelSort extends PApplet{

	public static final int LEFT = 37;
	public static final int RIGHT = 39;
	public static final int SCROLL = 3;

	public static void main (String args[]){
		main("PixelSort");
	}

	public PImage img;
	String img_file = "img3.jpg";
	String img_url = java.nio.file.Paths.get("").toAbsolutePath().toString()+"\\"+img_file;

	public void settings() {
		img = loadImage(img_url);
		size(img.width+20, img.height);
	}

	public void setup() {
		Class c = this.getClass();
		System.out.println(c.getPackage());
		settings();
		image(img, 0, 0);
		loadPixels();
		//mainSort();
		//updatePixels();
	}

	public void mainSort() {
		// creat sorting object
		for (int column_number = 0; column_number < img.width-1; column_number ++) {
			// pass column_number to object
			sort_column(column_number);
		}
	}

	public void sort_column(int column_number) {
		int column_start = column_number;
		int column_end = ((img.height)*img.width)+column_start;
		int sub_start = -1, sub_end = -1;
		boolean blue = false, white = false;

		// iterates down the column
		for (int pixel_pointer=column_start, row=0; pixel_pointer < column_end; pixel_pointer+=(width), row++) {
			int pixel = pixels[pixel_pointer];

			// Find subarray
			// identifying beginning of subarray
			if (sub_start == -1) {
				if (!isWhite(pixel)) {
					sub_start = pixel_pointer;
				}
			}

			// identifying end of subarray
			else if (sub_end == -1) {
				if (isWhite(pixel)) {
					sub_end = pixel_pointer;
				}
				else if (row == height-1 ){
					sub_end = pixel_pointer;
				}
			}

			// Sort subarray
			if (sub_end > -1) {
				// record indexs of the subarray pixels to map_array
				int rows_count = (sub_end-sub_start)/width;
				int[] map_array = new int[rows_count];
				int[] sub_array = new int[rows_count];
				for ( int sub_pointer = sub_start, count = 0; count < rows_count; sub_pointer += width, count ++ ){
					map_array[count] = sub_pointer;
					sub_array[count] = pixels[sub_pointer];
				}
				sub_array = sort(sub_array);
				sub_array = reverse(sub_array);

				// put subarray pixels into pixels[], original index stored in map
				for( int map_pointer = 0; map_pointer < map_array.length; map_pointer ++ )
					pixels[map_array[map_pointer]] = sub_array[map_pointer];

				///////// MARKERS
				//pixels[sub_start] = color(0xff0000);
				//pixels[sub_end] = color(0x00ff00);
				/////////				

				//reset values
				sub_start = -1; sub_end = -1;
				blue = false; white = false;

			}
		}
	}

	public void draw(){
		int i = mouseX + (mouseY*width); //https://www.gamedev.net/forums/topic/592791-index-to-2d-coordinates/
		fill(pixels[i]);
		strokeWeight(1);
		rect(width-21, 0, 20 ,20);
	}

	public void mouseClicked(){
		 if (mouseButton == LEFT){
		 	mainSort();
		 	updatePixels();
		 } else if (mouseButton == RIGHT){
		 	reset();
		 }
	}



	public void reset(){
		clear();
		background(200);
		image(img, 0 ,0);
		loadPixels();
	}

	private boolean isBlue(int pixel) {
		boolean r = false;
		if ( blue(pixel) > red(pixel) &&
				blue(pixel) > green(pixel) &&
				red(pixel) <= blue(pixel)*0.75 &&
				blue(pixel) > 100 ) r = true;
		return r;
	}

	private boolean isWhite(int pixel) {
		int[] av = average(pixel);
		int mean = av[0];
		int stdev = av[1];
		return (pixel == color(255)) || (mean > 120 && stdev < 20);
	}

	private boolean isBlack(int pixel) {
		int[] av = average(pixel);
		int mean = av[0];
		int stdev = av[1];
		return (pixel == color(0)) || (mean < 65) || (mean < 130 && stdev < 15);
	}

	private int[] average(int pixel){
		float[] rgb = new float[]{red(pixel), green(pixel), blue(pixel)};
		float mean = 0.0f;
		for(float x : rgb) mean += x;
		mean = mean/rgb.length;
		float temp = 0;
		for(float x :rgb) temp += (x-mean)*(x-mean);
		float variance = temp/(rgb.length-1);
		int stdev = (int)Math.sqrt((double)variance);
		return new int[]{(int)mean, stdev};
	}
}