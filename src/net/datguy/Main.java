package net.datguy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Path inPath = Paths.get("img/in");
        Path outPath = Paths.get("img/out");

        DirectoryStream<Path> inFolder;
        DirectoryStream<Path> outFolder;

        ArrayList<String> names;
        ArrayList<BufferedImage> ins;
        ArrayList<BufferedImage> outs;

        try {
            inFolder = Files.newDirectoryStream(inPath);
            outFolder = Files.newDirectoryStream(outPath);
        } catch (IOException x) {
            System.err.println(x);
            return;
        }

        try {
            for (Path file : outFolder) {
                Files.delete(file);
            }
            outFolder.close();
            outFolder = Files.newDirectoryStream(outPath);
        } catch (IOException x) {
            System.err.println(x);
        }

        ins = new ArrayList<>();
        names = new ArrayList<>();

        try {
            for (Path file : inFolder) {
                ins.add(ImageIO.read(file.toFile()));
                names.add(file.toFile().getName().split("\\.")[0]);
            }
            inFolder.close();
        } catch (IOException x) {
            System.err.println(x);
        }

        outs = new ArrayList<>();

        for (BufferedImage i : ins) {
            if (i != null)
                outs.add(to20by20Greyscale(i));
            else
                outs.add(null);
        }

        try {
            for (int o = 0; o < outs.size(); o++) {
                if (outs.get(o) != null) {
                    File outTemp = new File("img/out/" + names.get(o) + ".png");
                    ImageIO.write(outs.get(o), "png", outTemp);
                }
            }
        } catch (IOException x) {
            System.err.println(x);
        }

        try {
            inFolder.close();
            outFolder.close();
        } catch (IOException x) {
            System.err.println(x);
            System.err.println("WARNING: Potential Memory Leak");
        }
    }

    public static BufferedImage to20by20Greyscale(BufferedImage input) {
        Image temp = input.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        BufferedImage temp2 = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D temp3 = temp2.createGraphics();
        temp3.drawImage(temp, 0, 0, null);
        temp3.dispose();

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                int pixel = temp2.getRGB(i, j);
                int A = (pixel >> 24) & 0xff;
                int R = (pixel >> 16) & 0xff;
                int G = (pixel >> 8) & 0xff;
                int B = pixel & 0xff;

                int avg = R + G * 2 + B;
                avg /= 4;
                R = avg;
                G = avg;
                B = avg;

                pixel = (A << 24) | (R << 16) | (G << 8) | B;
                temp2.setRGB(i, j, pixel);
            }
        }

        return temp2;
    }
}
