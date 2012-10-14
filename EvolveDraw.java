import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;
import javax.swing.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;

public class EvolveDraw extends JPanel implements Runnable, FitnessDelegate
{
 //polygons
 protected final int VERTICES;
 protected final int N_POLY;
 
 //nao mude COLOR_COMP!
 protected final int COLOR_COMP = 4;
 protected final int MIN_ALPHA;
 protected final int MAX_ALPHA;
 protected PolygonColor[] polygons;
 protected int gen = 0;
 protected Color bgColor = Color.white;
 
 //algorithm params
 protected float MUTATION_RATE;
 protected BinaryIntChromosome best;
 protected boolean RANDOMIZE_START;
 
 //images
 protected final String TARGET_FILE;
 protected int[] offImagePixels; 
 protected BufferedImage targetImage;
 protected BufferedImage offImage; 
 protected int[] targetPixels;
 protected int[] redTarget;
 protected int[] greenTarget;
 protected int[] blueTarget;
 protected int[] alphaTarget;
 protected int w;
 protected int h;
 
 //save drawings
 protected final int PHOTO_INTERVAL_SEC;
    protected long lastPhotoTime = 0L;
 
 public EvolveDraw()
 {
  new Params();
  TARGET_FILE = Params.TARGET_FILE;
  VERTICES = Params.VERTICES;
  N_POLY = Params.N_POLY;    
  MUTATION_RATE = Params.MUTATION_RATE;
  PHOTO_INTERVAL_SEC = Params.PHOTO_INTERVAL_SEC;
  RANDOMIZE_START = Params.RANDOMIZE_START;
  MIN_ALPHA = Params.MIN_ALPHA;
  MAX_ALPHA = Params.MAX_ALPHA;
  
  this.setDoubleBuffered(true);
  this.setBackground(bgColor);
  this.loadTarget();
 }
 
 private void loadTarget()
 {
  this.targetImage = ImageUtils.imageToBufferedImage(ImageUtils.loadJPG(TARGET_FILE));
  this.w = targetImage.getWidth();
  this.h = targetImage.getHeight();
  this.setPreferredSize(new Dimension(w, h));
  
  this.offImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);  
  this.targetPixels = new int[w * h];
  this.offImagePixels = new int[w * h];
  this.targetImage.getRGB(0, 0, w, h, targetPixels, 0, w);
  
  this.redTarget = new int[w * h];
  this.greenTarget = new int[w * h];
  this.blueTarget = new int[w * h];
  this.alphaTarget = new int[w * h];
    
  for (int i = 0; i < this.targetPixels.length; i++)
  {
   int pixel = targetPixels[i];
   int alpha = (pixel >> 24) & 0xff;
   int red = (pixel >> 16) & 0xff;
   int green = (pixel >> 8) & 0xff;
   int blue = (pixel) & 0xff;

   this.redTarget[i] = red;
   this.greenTarget[i] = green;
   this.blueTarget[i] = blue;
   this.alphaTarget[i] = alpha;
  } 
 }
 
 public void run()
 { 
 
  int[][] bounds = PolygonDecoder.getChromosomeBounds(w, h,
  VERTICES, N_POLY, COLOR_COMP, MIN_ALPHA, MAX_ALPHA);
  HillClimb evolution = new HillClimb(bounds[0], bounds[1], this,  MUTATION_RATE, null, RANDOMIZE_START);
   
  do
  {
   CountDownLatch signal = new CountDownLatch(1);
   evolution.signal = signal;
   
   new Thread(evolution).start();
   try 
   {
    signal.await();
   }
   catch(InterruptedException ex)
   {
    ex.printStackTrace();
   }   
    
   best = evolution.getBest();     
   polygons = PolygonDecoder.chromosomeToPolygons(best.fenotype, VERTICES, N_POLY, COLOR_COMP);
   repaint();
        
   gen++;
   checkSnap();
      
  } while (true);
   
 }
   
 public void paintComponent(Graphics g)
 {
  super.paintComponent(g);
  if (best == null)
   return;     
   
   ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
   RenderingHints.VALUE_ANTIALIAS_ON);
   
   for (PolygonColor pc : polygons)
   {
    g.setColor(pc.color);
    g.fillPolygon(pc.polygon);
   }      
 }
 
 public BufferedImage createImage(JPanel panel) 
 {
  int wid = panel.getWidth();
  int heig = panel.getHeight();
  BufferedImage bi = new BufferedImage(wid, heig, BufferedImage.TYPE_INT_ARGB);
  Graphics2D g = bi.createGraphics();
  g.setBackground(bgColor);
  g.clearRect(0, 0, wid, heig);
  panel.paint(g);
  g.dispose();
    
  return bi;
 }
 
 protected void checkSnap()
 {
  long time = System.currentTimeMillis();
  long interval  = time - lastPhotoTime;
  
  if (interval >= PHOTO_INTERVAL_SEC * 1000L)
  {
   this.lastPhotoTime = time;
   ImageUtils.saveJPG(createImage(this), TARGET_FILE + gen + ".jpg");
  }
 }
 
 public float getFitness(int[] fenotype)
 {
  Graphics2D g = this.offImage.createGraphics();
  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  RenderingHints.VALUE_ANTIALIAS_ON);
  g.setBackground(bgColor);
  g.clearRect(0, 0, w, h);       
          
  PolygonColor[] polygons = PolygonDecoder.chromosomeToPolygons(fenotype, VERTICES, N_POLY,  COLOR_COMP);
  for (PolygonColor pc : polygons)
  {
   g.setColor(pc.color);
   g.fillPolygon(pc.polygon);
  }
  
  g.dispose();
  this.offImage.getRGB(0, 0, w, h, offImagePixels, 0, w);
        
  float diff = 0.0f;
  for (int i = 0; i < offImagePixels.length; i++)
  {
   int pixelOff = offImagePixels[i];
   int alphaOff = (pixelOff >> 24) & 0xff;
   int redOff = (pixelOff >> 16) & 0xff;
   int greenOff = (pixelOff >> 8) & 0xff;
   int blueOff = (pixelOff) & 0xff;
   
   int alp = alphaTarget[i] - alphaOff;
   int red = redTarget[i] - redOff;
   int blue = blueTarget[i] - blueOff;
   int green = greenTarget[i] - greenOff;
   
   diff += alp * alp + red * red + blue * blue + green * green;                       
  }
        
   return 1.0f/(diff + 1.0f);
 }
  
 //executa
 public static void main (String[] args)
 {
  JFrame frame = new JFrame("Evolve Draw");
  final EvolveDraw panel = new EvolveDraw();         
  frame.getContentPane().add(panel);
  frame.pack();
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frame.setVisible(true);  
  frame.setResizable(false);
    
  Thread runnable = new Thread(panel);  
  runnable.start();  
 }
 
} //fim da classe
