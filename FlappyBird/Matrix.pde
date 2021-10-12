import java.util.Random;

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
