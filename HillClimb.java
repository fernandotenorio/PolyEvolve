import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class HillClimb implements Runnable
{ 
 protected BinaryIntChromosome current;
 protected BinaryIntChromosome best;
 protected float mutationRate;
 protected FitnessDelegate delegate; 
 protected Random random = new Random();
 protected CountDownLatch signal;
 
 public HillClimb(int[] lower, int[] upper, FitnessDelegate delegate, float mutationRate,  CountDownLatch signal, boolean randomizeStart)
 {
  this.delegate = delegate;
  this.mutationRate = mutationRate;
  this.signal = signal;
  
  current = new BinaryIntChromosome(lower, upper, randomizeStart ? random : null);
  current.decode();
  current.fitness = delegate.getFitness(this.current.fenotype);
  best = current;
 }
 
 public void run()
 {
  evolve();
  if (signal != null)
   signal.countDown();
 }
 
 public void evolve()
 {
  BinaryIntChromosome copyIndiv = current.duplicate();
  copyIndiv.mutate(random, mutationRate);
  copyIndiv.decode();
  copyIndiv.fitness = delegate.getFitness(copyIndiv.fenotype);
  
  if (copyIndiv.fitness > current.fitness)
  {
   current = copyIndiv;
   best = copyIndiv;
  }
 }
 
 public BinaryIntChromosome getBest()
 {
  return best;
 }
}
