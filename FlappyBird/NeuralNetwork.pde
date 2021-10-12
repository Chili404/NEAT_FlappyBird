import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork {
    int inputNodes;
    int hiddenNodes;
    int outputNodes;
    double lr = 0.1;

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
            hidden.data[i][0] = 1.0/(1+Math.exp(-hidden.data[i][0]));
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
