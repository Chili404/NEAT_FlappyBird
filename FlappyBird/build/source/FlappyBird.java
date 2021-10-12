import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Random; 
import java.util.Arrays; 
import java.util.Random; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FlappyBird extends PApplet {

final int TOTAL = 1000;
ArrayList<Bird> b = new ArrayList();
int wid = 400;
int rez = 20;
int score = 0;
boolean jumping = false;
PVector gravity = new PVector(0, 0.5f);
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
Matrix m = new Matrix();

public void setup() {
  
  for(int i = 0; i < TOTAL; i++) {
    b.add(new Bird());
  }
  pipes.add(new Pipe());
}

public void draw() {
  println(random(-1,1));
  background(0);

  if (frameCount % 75 == 0) {
    pipes.add(new Pipe());
  }

  /*if (keyPressed) {
    PVector up = new PVector(0, -5);
    b.applyForce(up);
  }*/


  if(b.size() == 0) {
      nextGeneration();
  }

  boolean safe = true;

  for (int i = pipes.size() - 1; i >= 0; i--) {
    Pipe p = pipes.get(i);
    p.update();

    for(int j = b.size() - 1; j >=0; j--) {
      if (p.hits(b.get(j))) {
        b.get(j).alive = false;
        listRemove(b, j);

      }
    }
    p.show(false);
    /*


    if (p.x < -p.w) {
      pipes.remove(i);
    }
    */
  }

  for(int i = 0; i < b.size(); i++) {
    if(b.size() > 0) {
      b.get(i).think(pipes);
      b.get(i).update();
      b.get(i).show();
    }
  }

  if (b.size() > 0) {
    score++;
  } else {
    score -= 50;
  }

  fill(255, 0, 255);
  textSize(64);
  text(score, width/2, 50);
  score = constrain(score, 0, score);
}
class Bird {
  PVector pos;
  PVector vel;
  PVector acc;
  float r = 16;
  double score = 0;
  boolean alive = true;

  NeuralNetwork brain;

  Bird() {
    pos = new PVector(50, height/2);
    vel = new PVector(0, 0);
    acc = new PVector();

    this.brain = new NeuralNetwork(4, 4, 2);
  }

  

  public void applyForce(PVector force) {
    acc.add(force);
  }

  public void think(ArrayList<Pipe> pipes) {
    Pipe closest = null;
    double closestD = Integer.MAX_VALUE;
    for(int i = 0; i < pipes.size(); i++) {
      double d = pipes.get(i).x - pos.x;
      if(d < closestD && d > 0) {
        closest = pipes.get(i);
        closestD = d;
      }
    }

    double[] inputs = new double[4];
    inputs[0] = pos.y;
    inputs[1] = closest.top / height;
    inputs[2] = closest.bottom / height;
    inputs[3] = closest.x / width;


    Matrix outputs = brain.feedForward(new Matrix(inputs));
    if(outputs.data[0][0] > outputs.data[1][0]) {
      PVector up = new PVector(0, -5);
      applyForce(up);
    }
  }

  public void update() {
    applyForce(gravity);
    pos.add(vel);
    vel.add(acc);
    vel.limit(4);
    acc.mult(0);

    if (pos.y > height) {
      pos.y = height;
      vel.mult(0);
    }
    if(alive) {
      score++;
    }
  }

  public void show() {
    stroke(255);
    fill(255, 100);
    ellipse(pos.x, pos.y, r*2, r*2);
  }
}
ArrayList<Bird> bird_score = new ArrayList();
int adding = 0;
public void listRemove(ArrayList<Bird> b, int index) {
  //println(b.size() + " " + b.get(index));
  ArrayList<Bird> temp = new ArrayList();
  for(int i = 0; i < b.size(); i++) {
    if(i != index) {
      temp.add(b.get(i));

    }else {
        bird_score.add(b.get(i));
    }
  }
  b.clear();
  println(temp.size());
  for(int i = 0; i < temp.size() ; i++) {
    b.add(temp.get(i));
  }
  //println(b.size() + " " + b.get(index));
}

public void nextGeneration() {
  double total_score = 0;
  println(bird_score.size());
  for(int i = 0; i < bird_score.size(); i++) {
    total_score += bird_score.get(i).score;
  }
  for(int i = 0; i < bird_score.size(); i++) {
    bird_score.get(i).score /= total_score;
  }


  for(int i = 0; i < TOTAL; i++) {
    Bird temp = new Bird();
    int index = acceptReject();
    temp.brain = bird_score.get(index).brain.copy();
    temp.brain.mutate(0.1f);
    b.add(temp);
  }
}

public int acceptReject() {
    double threshold = random(1);
    int count = 0;
    while(threshold > 0) {
      threshold -= bird_score.get(count).score;
      count++;
    }
    count--;
    return count;
}


public class Matrix {
    int rows;
    int cols;
    double[][] data;

    Random rand = new Random();

    public Matrix() {

    }

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new double[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                data[i][j] = random(-1,1);
            }
        }
    }

    public Matrix(double[][] mat) {
        this.rows = mat.length;
        this.cols = mat[0].length;
        data = new double[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                this.data[i][j] = mat[i][j];
            }
        }

    }

    public Matrix(Matrix mat) {
        this.rows = mat.rows;
        this.cols = mat.cols;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                this.data[i][j] = mat.data[i][j];
            }
        }
    }

    public Matrix(double[] mat) {
        this.rows = mat.length;
        this.cols = 1;
        data = new double[rows][cols];

        int counter = 0;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                data[i][j] = mat[counter];
                counter++;
            }
        }
    }

    public void add(Matrix mat) {
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                data[i][j] += mat.data[i][j];
            }
        }
    }

    /*
    public static Matrix add(Matrix data, Matrix data2) {
        Matrix result = new Matrix();
        for(int i = 0; i < data.data.length; i++) {
            for(int j = 0; j < data.data[0].length; j++) {
                result.data[i][j] = data.data[i][j] + data2.data[i][j];
            }
        }
        return result;
    }
    */

    //non static implementation
    public Matrix addS(Matrix data2) {
        Matrix data = this;
        Matrix result = new Matrix();
        for(int i = 0; i < data.data.length; i++) {
            for(int j = 0; j < data.data[0].length; j++) {
                result.data[i][j] = data.data[i][j] + data2.data[i][j];
            }
        }
        return result;
    }

    public void subtract(Matrix mat) {
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                data[i][j] -= mat.data[i][j];
            }
        }
    }
    /*
    public static Matrix subtract(Matrix data, Matrix data2) {
        Matrix result = new Matrix(data.data.length, data.data[0].length);
        for(int i = 0; i < data.data.length; i++) {
            for(int j = 0; j < data.data[0].length; j++) {
                result.data[i][j] = data.data[i][j] - data2.data[i][j];
            }
        }
        return result;
    }
    */

    //non-static implementation
    public Matrix subtractS(Matrix data2) {
        Matrix data = this;
        Matrix result = new Matrix(data.data.length, data.data[0].length);
        for(int i = 0; i < data.data.length; i++) {
            for(int j = 0; j < data2.data[0].length; j++) {
                result.data[i][j] = data.data[i][j] - data2.data[i][j];
            }
        }
        return result;
    }

    /*
    public static Matrix transpose(Matrix mat) {
        double[][] result = new double[mat.cols][mat.rows];
        for(int i = 0; i < mat.data[0].length; i++) {
            for(int j = 0; j < mat.data.length; j++) {
                result[i][j] = mat.data[j][i];
            }
        }
        return new Matrix(result);
    }
    */

    //non-static implementation
    public Matrix transposeS () {
        Matrix mat = this;
        double[][] result = new double[mat.cols][mat.rows];
        for(int i = 0; i < mat.data[0].length; i++) {
            for(int j = 0; j < mat.data.length; j++) {
                result[i][j] = mat.data[j][i];
            }
        }
        return new Matrix(result);
    }


    public double[] flatten() {
        double[] result = new double[data.length * data[0].length];
        int count = 0;
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                result[count] = data[i][j];
                count++;
            }
        }
        return result;
    }

    /*
    public static Matrix multiply(Matrix m1, Matrix m2) {
        if(m2.rows != m1.cols) {
            System.err.print("Dimensions do not match\n");
            return new Matrix();
        }
        double[][] data2 = m2.data;
        Matrix result = new Matrix(m1.data.length, data2[0].length);
        for(int i = 0; i < m1.data.length; i++) {
            for(int j = 0; j < m2.data[0].length; j++) {
                double sum = 0;
                for(int k = 0; k  < m2.data.length; k++) {
                    sum += m1.data[i][k] * data2[k][j];
                }
                result.data[i][j] = sum;
            }
        }

        return result;
    }
    */

    //Non-static implementation
    public Matrix multiplyS(Matrix m2) {
        Matrix m1 = this;
        if(m2.rows != m1.cols) {
            System.err.print("Dimensions do not match\n");
            return new Matrix();
        }
        double[][] data2 = m2.data;
        Matrix result = new Matrix(m1.data.length, data2[0].length);
        for(int i = 0; i < m1.data.length; i++) {
            for(int j = 0; j < m2.data[0].length; j++) {
                double sum = 0;
                for(int k = 0; k  < m2.data.length; k++) {
                    sum += m1.data[i][k] * data2[k][j];
                }
                result.data[i][j] = sum;
            }
        }

        return result;
    }


    public void hadegard(Matrix m2) {
        Matrix m1 = this;
        if(m1.rows == m2.rows && m1.cols == m2.cols) {
            for(int i = 0; i < m1.data.length; i++) {
                for (int j = 0; j < m2.data[0].length; j++) {
                    m1.data[i][j] = m1.data[i][j] * m2.data[i][j];
                }
            }
        }else {
            System.err.print("Mismathced hadegard dimensions");
        }
    }

    public void multiply(Matrix m2) {
        Matrix m1 = this;
        if(m2.rows != m1.cols) {
            System.err.print("Dimensions do not match\n");
        }else {
            double[][] data2 = m2.data;
            Matrix result = new Matrix(m1.data.length, data2[0].length);
            for (int i = 0; i < m1.data.length; i++) {
                for (int j = 0; j < m2.data[0].length; j++) {
                    double sum = 0;
                    for (int k = 0; k < m1.data.length; k++) {
                        sum += m1.data[i][k] * data2[k][j];
                    }
                    result.data[i][j] = sum;
                }
            }
            this.data = result.data;

        }

    }

    public void multiply(double n) {
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                data[i][j] *= n;
            }
        }
    }

    public void power(double n) {
        for(int i = 0; i < data.length;i++) {
            for(int j = 0; j < data[0].length; j++) {
                data[i][j] = Math.pow(data[i][j], n);
            }
        }
    }

    public Matrix copy () {
        return new Matrix(this.data);
    }


    public void print() {
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
    }
    public void printDim() {
        System.out.printf("[%d x %d]\n", rows, cols);
    }
}



public class NeuralNetwork {
    int inputNodes;
    int hiddenNodes;
    int outputNodes;
    double lr = 0.1f;

    Matrix hiddenData;
    Matrix weights_ih;
    Matrix weights_ho;
    Matrix bias_h;
    Matrix bias_o;


    public NeuralNetwork(NeuralNetwork nn) {
        this.inputNodes = nn.inputNodes;
        this.hiddenNodes = nn.inputNodes;
        this.outputNodes = nn.outputNodes;

        this.weights_ho = nn.weights_ho.copy();
        this.weights_ih = nn.weights_ih.copy();
        this.bias_o = nn.bias_o.copy();
        this.bias_h = nn.bias_h.copy();
    }
    public NeuralNetwork(int numInput, int numHidden, int numOutput) {
        this.inputNodes = numInput;
        this.hiddenNodes = numHidden;
        this.outputNodes = numOutput;

        this.weights_ih = new Matrix(hiddenNodes, inputNodes);
        this.weights_ho = new Matrix(numOutput, numHidden);

        this.bias_h = new Matrix(numHidden, 1);
        this.bias_o = new Matrix(numOutput, 1);

    }

    public Matrix feedForward(Matrix input) {
        //Feed data forward to hidden layer
        hiddenData = weights_ih.multiplyS(input);
        hiddenData.add(bias_h);
        activate(hiddenData);

        //Feed data from hidden layer to output layer
        Matrix output = weights_ho.multiplyS(hiddenData);
        output.add(bias_o);
        activate(output);

        return output;
    }

    public void train(Matrix inputs, double[] targets) {
        Matrix outputs = feedForward(inputs);

        //Output
        Matrix output_error = (new Matrix(targets)).subtractS(outputs);



        Matrix gradient = new Matrix(derivative(outputs));
        gradient.hadegard(output_error);
        gradient.multiply(lr);

        Matrix weights_ho_deltas = gradient.multiplyS(hiddenData.transposeS());
        this.bias_o.add(gradient);
        this.weights_ho.add(weights_ho_deltas);


        Matrix hidden_error = weights_ho.transposeS().multiplyS(output_error);
        Matrix hidden_gradient = new Matrix(derivative(hiddenData));
        hidden_gradient.hadegard(hidden_error);
        hidden_gradient.multiply(lr);

        this.bias_h.add(hidden_gradient);
        Matrix weights_ih_deltas = hidden_gradient.multiplyS(inputs.transposeS());
        this.weights_ih.add(weights_ih_deltas);

        //bias_h.print();
        //System.out.println();
        //bias_o.print();
        //System.out.println();
        //this.weights_ih.print();
        //System.out.println();
        //this.weights_ho.print();
    }


    //Leaky ReLu Activation
    /*
    private void activate(Matrix hidden) {
        for (int i = 0; i < hidden.data.length; i++) {
            if (hidden.data[i][0] < 0) {
                hidden.data[i][0] = 0.1 * hidden.data[i][0];
            }
        }
    }

    private double[] derivative(Matrix m) {
        double[] result= new double[m.data.length];
        for (int i = 0; i < m.data.length; i++) {
            if (m.data[i][0] < 0) {
                result[i] = 0.1;
            }else {
                result[i] = 1;
            }
        }
        return result;
    }
    */



    private void activate(Matrix hidden) {
        for (int i = 0; i < hidden.data.length; i++) {
            hidden.data[i][0] = 1.0f/(1+Math.exp(-hidden.data[i][0]));
        }
    }

    private double[] derivative(Matrix m) {
        double[] result= new double[m.data.length];
        for (int i = 0; i < m.data.length; i++) {
            result[i] = m.data[i][0]*(1-m.data[i][0]);
        }
        return result;
    }


    //NEAT Specific Functions
    public NeuralNetwork copy() {
        return new NeuralNetwork(this);

    }

    public void mutate(double mutationRate) {
        mutateInner(this.weights_ih, mutationRate);
        mutateInner(this.weights_ho, mutationRate);
        mutateInner(this.bias_h, mutationRate);
        mutateInner(this.bias_o, mutationRate);
    }

    private void mutateInner(Matrix param, double mutationRate) {
        Random rand = new Random();
        for(int i = 0; i < param.rows; i++) {
            for(int j = 0; j < param.cols; j++) {
                if(rand.nextDouble() < mutationRate) {
                    param.data[i][j] = rand.nextDouble() * 2 - 1;
                }
            }
        }
    }

}
class Pipe {
  float x;
  float top;
  float bottom;
  float w = 40;

  Pipe() {
    x = wid + w;
    top = random(100, height/2);
    bottom = random(100, height/2);
    while(top - bottom < 72) {
      top = random(100, height/2);
      bottom = random(100, height/2);
    }
  }

  public boolean hits(Bird b) {
    if ((b.pos.x > x) && (b.pos.x < (x + w))) {
      if ((b.pos.y < (top + b.r)) || (b.pos.y > (height - bottom - b.r))) {
        return true;
      }
    }
    return false;
  }

  public void update() {
    x -= 3;
  }

  public void show(boolean hit) {
    stroke(255);

    if (hit) {
      fill(255, 0, 0);
    } else {
      fill(255);
    }

    rect(x, 0, w, top);
    rect(x, height - bottom, w, bottom);
  }
}
  public void settings() {  size(400, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FlappyBird" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
