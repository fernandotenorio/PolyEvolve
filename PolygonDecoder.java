import java.awt.Color;
import java.awt.Polygon;

public class PolygonDecoder
{
 protected static PolygonColor[] chromosomeToPolygons(int[] data, int verts, int n, int colors)
 {  
  int index = 0;
  int genes = 2 *  verts + colors;
  int dim = genes * n;
  PolygonColor[] polygons = new PolygonColor[n];
  
  int[] x = new int[verts];
  int[] y = new int[verts];
  int[] col = new int[colors];
  
  for (int i = 0; i < dim; i += genes )
  {  
   //vertices
   int start = i;
   int end = start + verts;
   
   //x vertices
   int k = 0;
   for (int m = start; m < end; m++)
    x[k++] = data[m];
   
   start = end;
   end = start + verts;
   
   //y vertices
   k = 0;
   for (int m = start; m < end; m++)
    y[k++] = data[m];
    
   k = 0;
   //color
   for (int c = i + 2 * verts; c < i + 2 * verts + colors; c++)
   {
    col[k++] = data[c]; 
   }
   
   PolygonColor pc = new PolygonColor();
   pc.polygon = new Polygon(x, y, verts);
   pc.color = new Color(col[0], col[1], col[2], col[3]);
            
   polygons[index++] = pc;
  }
  
  return polygons;
 }
 
protected static int[][] getChromosomeBounds(int w, int h, int verts,
int n, int colors,int minAlpha, int maxAlpha)
 {    
  int genes = 2 *  verts + colors;
  int dim = genes * n;
  int[] minV = new int[dim];
  int[] maxV = new int[dim];
  
  for (int i = 0; i < dim; i += genes )
  {   
   int start = i;
   int end = start + verts;
   
   //x vertices
   for (int m = start; m < end; m++)
    maxV[m] = w;
   
   start = end;
   end = start + verts;
   
   //y vertices
   for (int m = start; m < end; m++)
    maxV[m] = h;
          
   //color
   for (int c = i + 2 * verts; c < i + 2 * verts + colors; c++)
   {    
    //alpha component
    if (c == i + 2 * verts + colors - 1)
    {
     minV[c] = minAlpha;
     maxV[c] = maxAlpha;         
    }
    //rgb component 
    else  
     maxV[c] = 255;   
   }
  }
  
  return new int[][]{minV, maxV};
 }
   
} //fim da classe
