import java.util.Random;

public class BinaryIntChromosome implements Comparable<BinaryIntChromosome>
{
 protected int[] minVals;
 protected int[] maxVals;
 protected int[] bitsPerVar;
 protected boolean[] genotype;
 protected int[] fenotype;
 protected float fitness; 
 protected int totalBits;
 
 public BinaryIntChromosome(int[] minVals, int[] maxVals, Random random)
 {
  this.minVals = minVals;
  this.maxVals = maxVals;
  this.bitsPerVar = new int[minVals.length];
  
  for (int i = 0; i < minVals.length; i++)
  {
   int nBits = maxVals[i] - minVals[i] + 1;
   double bits = Math.log(nBits)/Math.log(2); 
   bitsPerVar[i] = (int) Math.ceil(bits); 
   this.totalBits += bitsPerVar[i];
  }
  
  this.genotype = new boolean[totalBits];
  this.fenotype = new int[minVals.length];
  
  if (random != null)
   this.randomizeGenotype(random);
 }
 
 protected void randomizeGenotype(Random random)
 { 
  for (int i = 0; i < totalBits; i++)        
   genotype[i] = random.nextBoolean();  
 }
 
 protected static int binToDec(boolean[] string, int startIndex, int endIndex)
 {
  int val = 0;
  int slot = 0;
  for (int i = startIndex; i <= endIndex; i++)
  {
   if (string[i])
    val += (int) Math.pow(2, slot);
   slot++;
  }
  return val;
 }
 
 protected void mutate(Random random, float mutationRate)
 {
  for (int i = 0; i < genotype.length; i++)
  {
   float f = random.nextFloat();
   if (f < mutationRate)
    genotype[i] = !genotype[i];
  }
 }
 
 protected BinaryIntChromosome duplicate()
 {
  int[] lb = new int[minVals.length];
  int[] ub = new int[maxVals.length];
  
  for (int i = 0; i  < minVals.length; i++)
  {
   lb[i] = minVals[i];
   ub[i] = maxVals[i];
  }
  
  BinaryIntChromosome theCopy = new BinaryIntChromosome(lb, ub, null);  
  for (int i = 0; i < this.genotype.length; i++)
   theCopy.genotype[i] = this.genotype[i];
   
  for (int i = 0; i < this.fenotype.length; i++)
   theCopy.fenotype[i] = this.fenotype[i];
   
  return theCopy;
 }
 
 protected void decode()
 {
  int bitIndex = 0;
  for (int var = 0; var < bitsPerVar.length; var++)
  {
   int bitsToRead = bitsPerVar[var];
   int startIndex = bitIndex;
   int endIndex = startIndex + bitsToRead - 1; 
   int decVal = BinaryIntChromosome.binToDec(this.genotype, startIndex, endIndex);   
   int n = (int)(Math.pow(2, bitsToRead) - 1);      
   int range = maxVals[var] - minVals[var];
   this.fenotype[var] = this.minVals[var] + (range * decVal) / n;               
   bitIndex += bitsToRead;   
  }
 }
 
 public int compareTo(BinaryIntChromosome other)
 {      
  if (this.fitness > other.fitness)
    return 1;
  else if (this.fitness < other.fitness)
   return -1;
   
  return 0;
 }
 
} 
