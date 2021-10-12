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

  

  void applyForce(PVector force) {
    acc.add(force);
  }

  void think(ArrayList<Pipe> pipes) {
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

  void update() {
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

  void show() {
    stroke(255);
    fill(255, 100);
    ellipse(pos.x, pos.y, r*2, r*2);
  }
}
