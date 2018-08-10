@echo off
SET processing_lib=..\..\processing-2.2.1-windows32\processing-2.2.1\core\library\core.jar
SET processing_=".;%processing_lib%"

javac -cp %processing_% src\*.java
java -cp %processing_% src.PixelSort