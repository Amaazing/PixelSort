package src;

import processing.core.*;

public class Column_sorter{

	public void setup(){};
	PixelSort parent;
	int height;
	int width;
	int img_height;	
	int img_width;

	int column_start;
	int column_end;
	int sub_start = -1, sub_end = -1;
	int rows_count;
	int[] map_array;
	int[] sub_array;
	int col;

	Column_sorter(PixelSort ps){
		parent = ps;
		height = ps.height;
		width = ps.width;
		img_height = parent.img.height;
		img_width = parent.img.width;
	}

	public void sort_column(int column_number){
		col = column_number;
		column_start = column_number;
		column_end = ((img_height)*img_width)+column_start;

		// iterates down the column
		for (int pixel_pointer=column_start, row=0; pixel_pointer < column_end; pixel_pointer+=img_width, row++) {
			int pixel = parent.img.pixels[pixel_pointer];


			// Find subarray
			// identifying beginning of subarray
			if (sub_start == -1) {
				if (!this.isWhite(pixel)){
					sub_start = pixel_pointer;
				}
			}

			// identifying end of subarray
			else if (sub_end == -1) {
				if (isWhite(pixel)) {
					sub_end = pixel_pointer;
				}
			}

			// behaviour for matching very last row
			if (row == height-1){
				if (sub_start == -1) continue; // cannot start array on the last row
				if (sub_end == -1) sub_end = pixel_pointer; // subarray ends at last row
			}

			// Sort subarray
			if (sub_end > -1) {
				// record indexs of the subarray pixels to map_array
				rows_count = (sub_end-sub_start)/img_width;
				map_array = new int[rows_count];
				sub_array = new int[rows_count];
				for ( int sub_pointer = sub_start, count = 0; count < rows_count; sub_pointer += img_width, count ++ ){
					map_array[count] = sub_pointer;
					sub_array[count] = parent.img.pixels[sub_pointer];
				}
				sub_array = parent.sort(sub_array);
				//sub_array = parent.reverse(sub_array);

				// put subarray pixels into parent.img.pixels[], original index stored in map
				for( int map_pointer = 0; map_pointer < map_array.length; map_pointer ++ )
					parent.img.pixels[map_array[map_pointer]] = sub_array[map_pointer];

				///////// MARKERS
				//parent.img.pixels[sub_start] = parent.color(0xff0000);
				//parent.img.pixels[sub_end] = parent.color(0x00ff00);
				/////////				

				//reset values
				resetSubMarkers();
			}

		}
	}

	private final void resetSubMarkers(){
		this.sub_start = -1;
		this.sub_end = -1;
	}

	public final float red(int rgb){ return parent.red(rgb); }
	public final float blue(int rgb){ return parent.blue(rgb); }
	public final float green(int rgb){ return parent.green(rgb); }

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
		return (pixel == parent.color(255)) || (mean > 120 && stdev < 20);
	}

	private boolean isBlack(int pixel) {
		int[] av = average(pixel);
		int mean = av[0];
		int stdev = av[1];
		return (pixel == parent.color(0)) || (mean < 65) || (mean < 130 && stdev < 15);
	}

	private int[] average(int pixel){
		// https://stackoverflow.com/a/7988556/8279774
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