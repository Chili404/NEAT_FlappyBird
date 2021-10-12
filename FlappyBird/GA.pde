ArrayList<Bird> bird_score = new ArrayList();
int adding = 0;
void listRemove(ArrayList<Bird> b, int index) {
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
  for(int i = 0; i < temp.size() ; i++) {
    b.add(temp.get(i));
  }
  //println(b.size() + " " + b.get(index));
}

void nextGeneration() {
  double total_score = 0;
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
    temp.brain.mutate(0.1);
    b.add(temp);
  }
}

int acceptReject() {
    double threshold = random(1);
    int count = 0;
    while(threshold > 0) {
      threshold -= bird_score.get(count).score;
      count++;
    }
    count--;
    return count;
}
