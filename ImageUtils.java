import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;

public class ImageUtils
{
 public static BufferedImage imageToBufferedImage(Image img) 
 {
  BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null),  
  BufferedImage.TYPE_INT_RGB);
  Graphics2D g2 = bi.createGraphics();
  g2.drawImage(img, null, null);
   
  return bi;
 }
 
 public static void saveJPG(Image img, String filename) 
 {  
  BufferedImage bi = imageToBufferedImage(img);
  FileOutputStream out = null;
  try 
  { 
   out = new FileOutputStream(filename);
  } 
  catch (java.io.FileNotFoundException io) 
  { 
   System.out.println("File Not Found"); 
  }
  
  JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
  JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
  param.setQuality(1.0f, false); //ou 0.8f 
  encoder.setJPEGEncodeParam(param);
  try 
  { 
   encoder.encode(bi); 
   out.close(); 
  } 
  catch (java.io.IOException io) 
  {
   System.out.println("IOException"); 
  }
 }
 
 public static Image loadJPG(String filename) 
 {
  FileInputStream in = null;
  try 
  { 
   in = new FileInputStream(filename);
  }
  catch (java.io.FileNotFoundException io) 
  { 
   System.out.println("File Not Found"); 
  }
  JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
  BufferedImage bi = null;
  try 
  { 
   bi = decoder.decodeAsBufferedImage(); 
   in.close(); 
  } 
  catch (java.io.IOException io) 
  {
   System.out.println("IOException");
  }
  return bi;
 }
 
}
