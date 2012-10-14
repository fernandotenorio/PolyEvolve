import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Params 
{
 static int VERTICES = 0;
 static int N_POLY = 0;
 static float MUTATION_RATE = 0.0f;
 static int PHOTO_INTERVAL_SEC = 0;
 static String TARGET_FILE;
 static boolean RANDOMIZE_START = false;
 static int MIN_ALPHA = 0;
 static int MAX_ALPHA = 0;
 
 public Params() 
 { 
  Scanner scanner = new Scanner(this.getClass().getResourceAsStream("params.ini")); 
  HashMap<String, String> map = new HashMap<String, String>();
  
  while (scanner.hasNext()) 
  {
   map.put(scanner.next(), scanner.next());
  }
  
  TARGET_FILE = map.get("target_file");
  VERTICES = Integer.parseInt(map.get("vertices"));
  N_POLY = Integer.parseInt(map.get("n_poly"));    
  MUTATION_RATE = Float.parseFloat(map.get("mutation_rate"));
  PHOTO_INTERVAL_SEC = Integer.parseInt(map.get("photo_interval_sec"));
  RANDOMIZE_START = Boolean.parseBoolean(map.get("randomize_start"));
  MIN_ALPHA = Integer.parseInt(map.get("min_alpha"));
  MAX_ALPHA = Integer.parseInt(map.get("max_alpha"));
 }
 
} //fim da classe
