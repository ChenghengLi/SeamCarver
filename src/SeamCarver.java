/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

/**
 *
 * @author Chengheng Li Chen
 */
public class SeamCarver {

    private int width, height;
    private int[][] color;
    private double[][] energy;

    // Arrays and sinks for finding the shortest path through the image energy
    private double[][] distTo;
    private int[][] edgeTo;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {

        if (picture == null) {
            throw new IllegalArgumentException("Null argument");
        }

        width = picture.width();
        height = picture.height();

        color = new int[height][width];
        energy = new double[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color[i][j] = picture.get(j, i).getRGB();
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                energy[i][j] = energyCalc(j, i);
            }
        }
    }

    private double energyCalc(int x, int y) {
        if (!validateIndex(x, y)) {
            throw new IllegalArgumentException("Index error");
        }

        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) {
            return 1000.0;
        }

        Color u = new Color(color[y - 1][x]);
        Color d = new Color(color[y + 1][x]);
        Color l = new Color(color[y][x - 1]);
        Color r = new Color(color[y][x + 1]);

        return Math.sqrt(calc(u, d) + calc(l, r));

    }

    private double calc(Color first, Color second) {
        return Math.pow(first.getRed() - second.getRed(), 2)
                + Math.pow(first.getGreen() - second.getGreen(), 2)
                + Math.pow(first.getBlue() - second.getBlue(), 2);
    }

    private boolean validateIndex(int w, int h) {
        return !(w >= width() || h >= height() || w < 0 || h < 0);
    }

    // current picture
    public Picture picture() {
        Picture pic = new Picture(width, height);
        convert(pic);
        return pic;

    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int w, int h) {
        if (!validateIndex(w, h)) {
            throw new IllegalArgumentException("Index Error");
        }
        return energy[h][w];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        distTo = new double[height()][width()];
        edgeTo = new int[height()][width()];

        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                if (j == 0) {
                    distTo[i][j] = 1000.0;
                    edgeTo[i][j] = -1;
                } else {
                    distTo[i][j] = Double.POSITIVE_INFINITY;
                    edgeTo[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        for (int x = height() - 1; x > 0; x--) {
            for (int y = x; y <= height() - 1 && y - x <= width() - 2; y++) {

                int i = y;
                int j = y - x;

                if (i == height() - 1) {
                    relaxH(i, j, i, j + 1);
                    relaxH(i, j, i - 1, j + 1);
                } else if (i == 0) {
                    relaxH(i, j, i, j + 1);
                    relaxH(i, j, i + 1, j + 1);
                } else {
                    relaxH(i, j, i - 1, j + 1);
                    relaxH(i, j, i, j + 1);
                    relaxH(i, j, i + 1, j + 1);
                }
            }
        }

        for (int x = 0; x < width(); x++) {
            for (int y = x; y - x <= height() - 1 && y <= width() - 2; y++) {

                int i = y - x;
                int j = y;

                if (i == height() - 1) {
                    relaxH(i, j, i, j + 1);
                    relaxH(i, j, i - 1, j + 1);
                } else if (i == 0) {
                    relaxH(i, j, i, j + 1);
                    relaxH(i, j, i + 1, j + 1);
                } else {
                    relaxH(i, j, i - 1, j + 1);
                    relaxH(i, j, i, j + 1);
                    relaxH(i, j, i + 1, j + 1);
                }
            }
        }

        double minPath = Double.POSITIVE_INFINITY;
        int indexPath = -1;
        for (int x = 0; x <= height() - 1; x++) {
            if (distTo[x][width() - 1] < minPath) {
                minPath = distTo[x][width() - 1];
                indexPath = x;
            }

        }

        int[] res = new int[width()];

        res[width() - 1] = indexPath;

        for (int i = width() - 2; i >= 0; i--) {
            res[i] = edgeTo[indexPath][i + 1];
            indexPath = res[i];
        }

        edgeTo = null;
        distTo = null;

        return res;
    }

    private void relaxH(int x1, int y1, int x2, int y2) {
        if (validateIndex(y1, x1) && validateIndex(y2, x2)) {
            if (distTo[x2][y2] > distTo[x1][y1] + energy[x2][y2]) {
                distTo[x2][y2] = distTo[x1][y1] + energy[x2][y2];
                edgeTo[x2][y2] = x1;
            }
        }
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        distTo = new double[height()][width()];
        edgeTo = new int[height()][width()];

        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                if (i == 0) {
                    distTo[i][j] = 1000.0;
                    edgeTo[i][j] = -1;
                } else {
                    distTo[i][j] = Double.POSITIVE_INFINITY;
                    edgeTo[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        for (int x = width() - 1; x > -1; x--) {
            for (int y = x; y <= width() - 1 && y - x <= height() - 1; y++) {

                int i = y - x;
                int j = y;

                if (j == width() - 1) {
                    relaxV(i, j, i + 1, j - 1);
                    relaxV(i, j, i + 1, j);
                } else if (j == 0) {
                    relaxV(i, j, i + 1, j);
                    relaxV(i, j, i + 1, j + 1);
                } else {
                    relaxV(i, j, i + 1, j - 1);
                    relaxV(i, j, i + 1, j);
                    relaxV(i, j, i + 1, j + 1);
                }
            }
        }

        for (int x = 1; x < height(); x++) {
            for (int y = x; y - x <= width() - 1 && y <= height() - 2; y++) {

                int i = y;
                int j = y - x;

                if (j == width() - 1) {
                    relaxV(i, j, i + 1, j - 1);
                    relaxV(i, j, i + 1, j);
                } else if (j == 0) {
                    relaxV(i, j, i + 1, j);
                    relaxV(i, j, i + 1, j + 1);
                } else {
                    relaxV(i, j, i + 1, j - 1);
                    relaxV(i, j, i + 1, j);
                    relaxV(i, j, i + 1, j + 1);
                }
            }
        }

        double minPath = Double.POSITIVE_INFINITY;
        int indexPath = -1;
        for (int x = 0; x <= width() - 1; x++) {
            if (distTo[height() - 1][x] < minPath) {
                minPath = distTo[height() - 1][x];
                indexPath = x;
            }

        }

        int[] res = new int[height()];

        res[height() - 1] = indexPath;

        for (int i = height() - 2; i >= 0; i--) {
            res[i] = edgeTo[i + 1][indexPath];
            indexPath = res[i];
        }

        edgeTo = null;
        distTo = null;

        return res;
    }

    private void relaxV(int x1, int y1, int x2, int y2) {
        if (validateIndex(y1, x1) && validateIndex(y2, x2)) {
            if (distTo[x2][y2] > distTo[x1][y1] + energy[x2][y2]) {
                distTo[x2][y2] = distTo[x1][y1] + energy[x2][y2];
                edgeTo[x2][y2] = y1;
            }
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {

        if (height() <= 1 || seam == null || seam.length != width()) {
            throw new java.lang.IllegalArgumentException("Seam error");
        }

        int last = seam[0];
        for (int y : seam) {
            if (y >= height() || y < 0 || Math.abs(y - last) > 1) {
                throw new java.lang.IllegalArgumentException("Error");
            }
            last = y;
        }

        height--;

        int[][] newColor = new int[this.height()][this.width()];
        double[][] newEnergy = new double[this.height()][this.width()];

        for (int y = 0; y < width(); y++) {
            int breaker = seam[y];
            for (int x = 0; x < breaker; x++) {
                newColor[x][y] = color[x][y];
            }

            for (int x = breaker + 1; x < height() + 1; x++) {
                newColor[x - 1][y] = color[x][y];
            }
        }

        color = newColor;
        energy = newEnergy;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                energy[i][j] = energyCalc(j, i);
            }
        }

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

        if (width() <= 1 || seam == null || seam.length != height()) {
            throw new java.lang.IllegalArgumentException("Seam error");
        }

        int last = seam[0];
        for (int x : seam) {
            if (x >= width() || x < 0 || Math.abs(x - last) > 1) {
                throw new java.lang.IllegalArgumentException("Error");
            }
            last = x;
        }

        width--;

        int[][] newColor = new int[this.height()][this.width()];
        double[][] newEnergy = new double[this.height()][this.width()];

        for (int x = 0; x < height(); x++) {
            int breaker = seam[x];
            for (int y = 0; y < breaker; y++) {
                newColor[x][y] = color[x][y];
            }

            for (int y = breaker + 1; y < width() + 1; y++) {
                newColor[x][y - 1] = color[x][y];
            }
        }

        color = newColor;
        energy = newEnergy;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                energy[i][j] = energyCalc(j, i);
            }
        }
    }

    private void convert(Picture pic) {
        for (int x = 0; x < height(); x++) {
            for (int y = 0; y < width(); y++) {
                pic.setRGB(y, x, color[x][y]);
            }
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture("10x10.png");
        SeamCarver carver = new SeamCarver(picture);
        carver.removeHorizontalSeam(carver.findHorizontalSeam());
        System.out.println(carver.energy(1, 2));

    }

}
