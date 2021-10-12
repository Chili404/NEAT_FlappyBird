final int TOTAL = 1000;
ArrayList<Bird> b = new ArrayList();
int wid = 400;
int rez = 20;
int score = 0;
boolean jumping = false;
PVector gravity = new PVector(0, 0.5);
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
Matrix m = new Matrix();

void setup() {
  size(400, 800);
  for(int i = 0; i < TOTAL; i++) {
    b.add(new Bird());
  }
  pipes.add(new Pipe());
}

void draw() {
  println();
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
