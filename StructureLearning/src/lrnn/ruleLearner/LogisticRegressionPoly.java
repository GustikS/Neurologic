/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lrnn.ruleLearner;

import lrnn.learning.functions.Activations;

import java.util.Arrays;

/* A logistic regression algorithm for binary classification implemented using Newton's method and
 * a Wolfe condition based inexact line-search.
 *created by Alrecenk for inductivebias.com May 2014
 */
public class LogisticRegressionPoly {


    private final int maxiter = 10000;
    private final double tolerance = 0.000001;

    double w[]; //the weights for the logistic regression
    int degree; // degree of polynomial used for preprocessing

    //preprocessed list of input/output used for calculating errorMeasure and its gradients
    double input[][];
    double output[];

    //these evaluation counters increment on every call to errorMeasure, gradient, and hessian respectively
    public int feval, geval, heval;

    public static void main(String[] args) {
        double[][] x = new double[6][3];
        for (int i = 0; i < x.length; i++) {
            x[i][0] = i;
            x[i][1] = 1;
            x[i][2] = 1;
        }
        double[] y = new double[]{1, 1, 1, 0, 0, 1};
        boolean[] out = new boolean[y.length];
        for (int i = 0; i < y.length; i++) {
            out[i] = y[i] > 0;
        }
        LogisticRegressionPoly lr = new LogisticRegressionPoly(x, out, 1);
        System.out.println(Arrays.toString(lr.w));
        double error = lr.error(lr.w);
        System.out.println(error);
    }

    //create a logistic regression for binary classification on the given inputand output
    //with polynomial expansion of the given degree
    public LogisticRegressionPoly(double in[][], boolean out[], int degree) {

        /*
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[i].length; j++) {
                System.out.print(in[i][j] + ";");
            }
            System.out.println(out[i]?1:0);
        }
        */

        this.degree = degree;

        input = new double[in.length][];
        output = new double[out.length];

        for (int k = 0; k < in.length; k++) {
            input[k] = polynomial(in[k], degree);
        }

        int postotal = 0, negtotal = 0;
        double pos[] = new double[input[0].length];
        double neg[] = new double[input[0].length];
        //get totals for negative and positive points
        for (int k = 0; k < in.length; k++) {
            if (out[k]) {
                output[k] = 1;
                pos = add(pos, input[k]);
                postotal++;
            } else {
                neg = add(neg, input[k]);
                negtotal++;
            }
        }

        //for non-polynomial case use starting weights pointing from centroid of negatives to centroid of positives.
        if (postotal >= 1 && negtotal >= 1 && degree == 1) {
            double pp = 0, pn = 0, nn = 0;
            for (int k = 0; k < pos.length; k++) {
                pos[k] /= postotal;//scale totals to get center of each class
                neg[k] /= negtotal;
                pp += pos[k] * pos[k];
                pn += pos[k] * neg[k];// calculate relevant dot products
                nn += neg[k] * neg[k];
            }
            //assuming w = alpha * (poscenter- negcenter) with b in the last slot
            //solve for alpha and b so that poscenter returns 0.75 and negcenter returns 0.25
            double alphab[] = lineIntersection(pp - pn, 1, sinv(0.75), pn - nn, 1, sinv(0.25));
            if (alphab == null || Math.abs(alphab[0]) > 1000000000000.0) {
                alphab = new double[]{0, 1};
                //throw new ArithmeticException("the examples are not divisible at all - just calculate majority error!");
            }
            w = new double[input[0].length];
            for (int k = 0; k < w.length - 1; k++) {
                w[k] = alphab[0] * (pos[k] - neg[k]); // alpha * pos-neg
            }
            w[w.length - 1] = alphab[1]; // bias is on the end of w
        } else {
            w = new double[pos.length];
        }

        //run newton's method to get locally optimal weights
        w = newtonMethod(w, tolerance * input.length, maxiter);
        //dump data after the curve fitting is complete
        //input = null;
        //output = null;
    }

    //applies the logistic regression to predict a new point's probability of being in the positive class
    public double apply(double i[]) {
        return s(dot(w, polynomial(i, degree)));
    }

    //returns the errorMeasure of a logistic regressions with weights w on the given input and output
    //output should be in the form 0 for negative, 1 for positive
    public double error(double w[]) {
        feval++;//keep track of how many times this has been called
        double error = 0;
        for (int k = 0; k < input.length; k++) {
            double diff = s(dot(w, input[k])) - output[k];
            error += diff * diff;
        }
        return error;
    }

    //returns the gradient of errorMeasure with respect to weights
    //for a logistic regression with weights w on the given input and output
    //output should be in the form 0 for negative, 1 for positive
    public double[] gradient(double w[]) {
        geval++;//keep track of how many times this has been called
        double g[] = new double[w.length];
        for (int k = 0; k < input.length; k++) {
            double dot = dot(w, input[k]);
            double coef = 2 * (s(dot) - output[k]) * ds(dot);
            for (int j = 0; j < g.length; j++) {
                g[j] += input[k][j] * coef;
            }
        }
        return g;
    }

    //returns a numerically calculated gradient - approximation to above
    //used only for unit testing gradient, not called in final version
    public double[] numericalGradient(double w[], double epsilon) {
        double g[] = new double[w.length];
        for (int j = 0; j < g.length; j++) {
            w[j] += epsilon;
            g[j] = error(w);
            w[j] -= 2 * epsilon;
            g[j] -= error(w);
            w[j] += epsilon;
            g[j] /= 2 * epsilon;
        }
        return g;
    }

    //returns the hessian (gradient of gradient) of errorMeasure with respect to weights
    //for a logistic regression with weights w on the given input and output
    //output should be in the form 0 for negative, 1 for positive
    public double[][] hessian(double w[]) {
        heval++;//keep track of how many times this has been called
        double h[][] = new double[w.length][];
        //second derivative matrices are always symmetric so we only need triangular portion
        for (int j = 0; j < h.length; j++) {
            h[j] = new double[j + 1];
        }
        for (int k = 0; k < input.length; k++) {
            //calculate coefficient{
            double dot = dot(w, input[k]);
            double ds = ds(dot);
            double coef = 2 * (ds * ds + dds(dot) * (s(dot) - output[k]));
            for (int j = 0; j < h.length; j++) {// add x * x^t * coef to hessian
                for (int l = 0; l <= j; l++) {
                    h[j][l] += input[k][j] * input[k][l] * coef;
                }
            }
        }
        return h;
    }

    //returns a numerically calculated hessian - approximation to above
    //used only for unit testing hessian, not called in final version
    public double[][] numericalHessian(double w[], double epsilon) {
        double h[][] = new double[w.length][];
        for (int j = 0; j < h.length; j++) {
            w[j] += epsilon;
            h[j] = gradient(w);
            w[j] -= 2 * epsilon;
            h[j] = subtract(h[j], gradient(w));
            w[j] += epsilon;
            for (int k = 0; k < w.length; k++) {
                h[j][k] /= 2 * epsilon;
            }
        }
        return h;
    }

    //sigmoid/logistic function
    public static double s(double x) {
        return Activations.sigmoid(x);
        /*
        if (x > 100) return 1.0;
        if (x < -100) return 0.0;
        double ex = Math.exp(x);
        return ex / (ex + 1);
        */
    }

    //derivative of sigmoid/logistic function
    public static double ds(double x) {
        return Activations.sigmoidDerived(x);
        /*
        if (x > 100) return 0.0;
        if (x < -100) return 0.0;
        double ex = Math.exp(x);
        return ex / ((ex + 1) * (ex + 1));
        */
    }

    //second derivative of sigmoid/logistic function
    public static double dds(double x) {
        double ex = Math.exp(-6 * x - 0.5);
        return (ex * (1 - ex)) / ((ex + 1) * (ex + 1) * (ex + 1));
    }

    //inverse of sigmoid/logistic function
    public static double sinv(double y) {
        return Math.log(y / (1 - y));
    }

    //starting from w0 searches for a weight vector using gradient descent
    //and Wolfe condition line-search until the gradient magnitude is below tolerance
    //or a maximum number of iterations is reached.
    public double[] gradientDescent(double w0[], double tolerance, int maxiter) {
        double w[] = w0;
        double gradient[] = gradient(w0);
        int iteration = 0;
        while (Math.sqrt(dot(gradient, gradient)) > tolerance && iteration < maxiter) {
            iteration++;
            //calculate step-size in direction of negative gradient
            double alpha = stepSize(this, w, scale(gradient, -1), 1, 500, 0.1, 0.9);
            w = add(w, scale(gradient, -alpha)); // apply step
            gradient = gradient(w); // get new gradient
        }
        return w;
    }


    //starting from w0 searches for a weight vector using Newton's method
    //and Wolfe condition line-search until the gradient magnitude is below tolerance
    //or a maximum number of iterations is reached.
    public double[] newtonMethod(double w0[], double tolerance, int maxiter) {
        double w[] = w0;
        double gradient[] = gradient(w0);
        int iteration = 0;
        while (Math.sqrt(dot(gradient, gradient)) > tolerance && iteration < maxiter) {
            iteration++;
            //get the second derivative matrix
            double hessian[][] = hessian(w);

            //perform an LDL decomposition and substitution to solve the system of equations Hd = -g  for the Newton step
            //(See Linear Least Squares Article on Inductive Bias for details on this technique)

            //calculate the LDL decomposition in place with D over top of the diagonal
            for (int j = 0; j < w.length; j++) {
                for (int k = 0; k < j; k++) {//D starts as Hjj then subtracts
                    hessian[j][j] -= hessian[j][k] * hessian[j][k] * hessian[k][k];//Ljk^2 * Dk
                }
                for (int i = j + 1; i < w.length; i++) {//L over the lower diagonal
                    for (int k = 0; k < j; k++) {//Lij starts as Hij then subtracts
                        hessian[i][j] -= hessian[i][k] * hessian[j][k] * hessian[k][k];//Ljk^2*D[k]
                    }
                    hessian[i][j] /= hessian[j][j];//divide Lij by Dj
                }
            }

            //check if D elements are all positive to make sure Hessian was positive definite and Newton step goes to a minimum
            boolean positivedefinite = true;
            for (int k = 0; k < w.length && positivedefinite; k++) {
                positivedefinite &= hessian[k][k] > 0;
            }

            //right hand side for Newton's method is negative gradient
            double[] newton = scale(gradient, -1);
            if (positivedefinite) { // if H was pd then get newton direction, otherwise leave it as -gradient
                //in-place forward substitution with L
                for (int j = 0; j < w.length; j++) {
                    for (int i = 0; i < j; i++) {
                        newton[j] -= hessian[j][i] * newton[i];
                    }
                }
                //Multiply by inverse of D matrix
                for (int k = 0; k < w.length; k++) {//inverse of diagonal
                    newton[k] /= hessian[k][k];//is 1 / each element
                }
                // in-place backward substitution with L^T
                for (int j = w.length - 1; j >= 0; j--) {
                    for (int i = j + 1; i < w.length; i++) {
                        newton[j] -= hessian[i][j] * newton[i];
                    }
                }
            }

            //calculate step-size
            double alpha = stepSize(this, w, newton, 1, 500, 0.001, 0.9);// then use it
            //apply step
            w = add(w, scale(newton, alpha));
            //calculate gradient for exit condition and next step
            gradient = gradient(w);
        }

        return w;
    }

    //Performs a binary search to satisfy the Wolfe conditions
    //returns alpha where next x =should be x0 + alpha*d
    //guarantees convergence as long as search direction is bounded away from being orthogonal with gradient
    //x0 is starting point, d is search direction, alpha is starting step size, maxit is max iterations
    //c1 and c2 are the constants of the Wolfe conditions (0.1 and 0.9 can work)
    public static double stepSize(LogisticRegressionPoly problem, double[] x0, double[] d, double alpha, int maxit, double c1, double c2) {

        //get errorMeasure and gradient at starting point
        double fx0 = problem.error(x0);
        double gx0 = dot(problem.gradient(x0), d);

        //bound the solution
        double alphaL = 0;
        double alphaR = 10000;

        for (int iter = 1; iter <= maxit; iter++) {
            double[] xp = add(x0, scale(d, alpha)); // get the point at this alpha
            double erroralpha = problem.error(xp); //get the errorMeasure at that point
            if (erroralpha >= fx0 + alpha * c1 * gx0) { // if errorMeasure is not sufficiently reduced
                alphaR = alpha;//move halfway between current alpha and lower alpha
                alpha = (alphaL + alphaR) / 2.0;
            } else {//if errorMeasure is sufficiently decreased
                double slopealpha = dot(problem.gradient(xp), d); // then get slope along search direction
                if (slopealpha <= c2 * Math.abs(gx0)) { // if slope sufficiently closer to 0
                    return alpha;//then this is an acceptable point
                } else if (slopealpha >= c2 * gx0) { // if slope is too steep and positive then go to the left
                    alphaR = alpha;//move halfway between current alpha and lower alpha
                    alpha = (alphaL + alphaR) / 2;
                } else {//if slope is too steep and negative then go to the right of this alpha
                    alphaL = alpha;//move halfway between current alpha and upper alpha
                    alpha = (alphaL + alphaR) / 2;
                }
            }
        }

        //if ran out of iterations then return the best thing we got
        return alpha;
    }

    //dot product
    public static double dot(double[] a, double[] b) {
        double c = 0;
        for (int k = 0; k < a.length; k++) {
            c += a[k] * b[k];
        }
        return c;
    }

    //returns a vector = to a*s
    public static double[] scale(double[] a, double s) {
        double[] b = new double[a.length];
        for (int k = 0; k < a.length; k++) {
            b[k] = a[k] * s;
        }
        return b;
    }

    //returns the sum of two vectors
    public static double[] add(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int k = 0; k < a.length; k++) {
            c[k] = a[k] + b[k];
        }
        return c;
    }

    //returns the sum of two vectors
    public static double[] subtract(double[] a, double[] b) {
        double[] c = new double[a.length];
        for (int k = 0; k < a.length; k++) {
            c[k] = a[k] - b[k];
        }
        return c;
    }

    //Creates a new input vector which is a 1, and each input raised to integer powers up to degree
    //when called with degree=1 this simply adds a bias value to the input vector
    //otherwise it creates a separable polynomial of the given degree
    public static double[] polynomial(double[] input, int degree) {
        double[] output = new double[1 + input.length * degree];
        int i = 0, k, j;
        for (k = 0; k < input.length; k++) {//each input
            for (j = 1; j <= degree; j++) {// raised to each power
                output[i] = (double) Math.pow(input[k], j);
                i++;
            }
        }
        output[i] = 1; //constant
        return output;
    }

    //returns the intersection of 2D lines given in standard form
    // a1*x + b1*y = c1  and a2*x + b2*y = c2
    public static double[] lineIntersection(double a1, double b1, double c1, double a2, double b2, double c2) {
        double det = a1 * b2 - a2 * b1;
        if (det == 0) {
            return null;
            //return new double[]{1, 1};
        } else {
            return new double[]{(c1 * b2 - b1 * c2) / det, (a1 * c2 - c1 * a2) / det};
        }
    }


}
