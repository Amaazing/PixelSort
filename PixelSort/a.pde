import java.util.Arrays;

PImage img;

void settings() {
  img = loadImage("img2.jpg");
  size(img.width, img.height);
}

void setup() {
  image(img, 0, 0);
  loadPixels();
  main_sort();
  updatePixels();
}

void main_sort() {
  for (int column_number = 0; column_number < img.width-1; column_number ++) {
    sort_column(column_number);
  }
}

void sort_column(int column_number) {
  int column_start = column_number;
  int column_end = ((img.height-1)*img.width)+column_start;
  int sub_array_start = -1, sub_array_end = -1;
  boolean blue = false, white = false;

  // iterates down a column
  for (int pixel_pointer = column_start; pixel_pointer < column_end; pixel_pointer+=(width)) {
    color pixel = pixels[pixel_pointer];
//---
    if (pixel_pointer > width*200){
      //System.exit(0);
    }
//---

    // get sub array start
    if (sub_array_start == -1) {
      if (isBlue(pixel) || isWhite(pixel)) { 
        sub_array_start = pixel_pointer;
        if (isBlue(pixel)) blue = true;
        else if (isWhite(pixel)) white = true;
      }
    }
    // get sub array end
    else if (sub_array_end == -1) {
      if (isBlack(pixel)) continue;//sub_array_end = pixel_pointer;
      else if (blue) {
        if (isWhite(pixel)) sub_array_end = pixel_pointer;
      } else if (white) {
        if (isBlue(pixel)) sub_array_end = pixel_pointer;
      }
    }
    
    //if (isBlack(pixel)) pixels[pixel_pointer] = color(255,0,0);
    
    if (sub_array_end > -1) {
      int rows_count = (sub_array_end-sub_array_start)/width;
      int[] sub_array = new int[rows_count];
      int[] map_array = new int[rows_count];
      for ( int pixels_sub_pointer = sub_array_start, count = 0; count < rows_count; pixels_sub_pointer += width, count ++ ){
        map_array[count] = pixels_sub_pointer;
        sub_array[count] = pixels[pixels_sub_pointer];
      }
      //================================================
      sub_array = sort(sub_array);
      sub_array = reverse(sub_array);
      //================================================
      for( int map_pointer = 0; map_pointer < map_array.length; map_pointer ++ ){
        pixels[map_array[map_pointer]] = sub_array[map_pointer];
      }
      
      //pixels[sub_array_start] = color(255,0,0);
      //pixels[sub_array_end] = color(0,255,0);
      
      //reset values
      sub_array_start = -1; sub_array_end = -1;
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

boolean isWhite(color pixel) {
  int[] av = average(pixel);
  int mean = av[0];
  int stdev = av[1];
  return (pixel == color(255)) || (mean > 170 && stdev < 25);
}

boolean isBlack(color pixel) {
  int[] av = average(pixel);
  int mean = av[0];
  int stdev = av[1];
  return (pixel == color(0)) || (mean < 65) || (mean < 130 && stdev < 15);
}

int[] average(color pixel){
  float[] rgb = new float[]{red(pixel), green(pixel), blue(pixel)};
  float mean = 0.0;
  for(float x : rgb) mean += x;
  mean = mean/rgb.length;
  float temp = 0;
  for(float x :rgb) temp += (x-mean)*(x-mean);
  float variance = temp/(rgb.length-1);
  int stdev = (int)Math.sqrt((double)variance);
  return new int[]{(int)mean, stdev};
}