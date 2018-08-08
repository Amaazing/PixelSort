import processing.core.*;
import java.util.Arrays;

public class PixelSort extends PApplet{

  public static void main (String args[]){
    main("PixelSort");
  }

  PImage img;
  String img_file = "img3.jpg";
  String img_url = java.nio.file.Paths.get("").toAbsolutePath().toString()+"\\"+img_file;

  public void settings() {
    img = loadImage(img_url);
    size(img.width, img.height);
  }

  public void setup() {
    settings();
    image(img, 0, 0);
    loadPixels();
    main_sort();
    updatePixels();
  }

  public void main_sort() {
    for (int column_number = 0; column_number < img.width-1; column_number ++) {
      sort_column(column_number);
      //if (column_number == 10) break;
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
        if (isWhite(pixel)) {
          sub_start = pixel_pointer;
        }
      }

      // identifying end of subarray
      else if (sub_end == -1) {
        if (!isWhite(pixel)) {
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


  boolean isBlue(int pixel) {
    boolean r = false;
    if ( blue(pixel) > red(pixel) &&
         blue(pixel) > green(pixel) &&
         red(pixel) <= blue(pixel)*0.75 &&
         blue(pixel) > 100 ) r = true;
    return r;
  }

  boolean isWhite(int pixel) {
    int[] av = average(pixel);
    int mean = av[0];
    int stdev = av[1];
    return (pixel == color(255)) || (mean > 120 && stdev < 20);
  }

  boolean isBlack(int pixel) {
    int[] av = average(pixel);
    int mean = av[0];
    int stdev = av[1];
    return (pixel == color(0)) || (mean < 65) || (mean < 130 && stdev < 15);
  }

  int[] average(int pixel){
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
