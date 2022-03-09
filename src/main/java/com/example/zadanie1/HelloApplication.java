package com.example.zadanie1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class HelloApplication extends Application {

    int histogram[] = new int[256];
    int histogramR[] = new int[256];
    int histogramB[] = new int[256];
    int histogramG[] = new int[256];

    int height = 0;
    int width = 0;

    File file = new File("C:\\Users\\Fisto\\Desktop\\Moje prace\\imgs\\pobrane.jpg");

    BufferedImage imgr;
    BufferedImage imgb;
    BufferedImage imgg;

    public HelloApplication() throws IOException {
    }

    @Override
    public void start(Stage stage) throws IOException {


        ImageView imageView = new ImageView();
        Button load = new Button("Load");
        load.setMinWidth(100);
        load.setOnAction(e->{
                FileChooser fileChooser = new FileChooser();
                file = fileChooser.showOpenDialog(stage);
                    try {
                        start(stage);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );

        BufferedImage img = ImageIO.read(file);
        height = img.getHeight();
        width = img.getWidth();


        Button save = new Button("Save");
        save.setMinWidth(100);
        save.setOnAction(e ->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("out.jpg");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "JPG", "jpg"));
            File output = fileChooser.showSaveDialog(stage);
            try {
                ImageIO.write(img,"jpg",output);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        imageView.setX(10);
        imageView.setY(10);
        int ratio = 0;
        if(width > 300){
            ratio = width/height;
            imageView.setFitWidth(300);
            imageView.setFitWidth(300*ratio);
        }

        imageView.setPreserveRatio(true);
        imageView.setImage(imgBin(img));

        Group root = new Group(imageView);

            final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        XYChart.Series series = new XYChart.Series();
        XYChart.Series seriesR = new XYChart.Series();
        XYChart.Series seriesG = new XYChart.Series();
        XYChart.Series seriesB = new XYChart.Series();

        for(int i = 0 ; i < 256; i++) {
            series.getData().add(new XYChart.Data(i, histogram[i]));
            seriesR.getData().add(new XYChart.Data(i, histogramR[i]));
            seriesG.getData().add(new XYChart.Data(i, histogramG[i]));
            seriesB.getData().add(new XYChart.Data(i, histogramB[i]));
        }

        lineChart.getData().addAll(series, seriesR, seriesG, seriesB );
        lineChart.setCreateSymbols(false);
        lineChart.setMaxWidth(600);
        lineChart.setMaxHeight(600);

        ImageView rgb1 = new ImageView(convertToFxImage(imgr));
        ImageView rgb2 = new ImageView(convertToFxImage(imgb));
        ImageView rgb3 = new ImageView(convertToFxImage(imgg));
        rgb1.setFitWidth(100);
        rgb1.setFitHeight((ratio == 0)?100:100*ratio);

        rgb2.setFitWidth(100);
        rgb2.setFitHeight((ratio == 0)?100:100*ratio);

        rgb3.setFitWidth(100);
        rgb3.setFitHeight((ratio == 0)?100:100*ratio);

        HBox rgbBox = new HBox(rgb1, rgb2, rgb3);
        VBox vbox = new VBox(10,root,rgbBox, load, save);
        vbox.setPadding(new Insets(10,10,10,10));
        HBox bighbox = new HBox(vbox,lineChart);
        Scene scene = new Scene(bighbox, 1000, 800);

        series.getNode().setStyle("-fx-stroke: #fffb00");
        seriesR.getNode().setStyle("-fx-stroke: #ff0000");
        seriesG.getNode().setStyle("-fx-stroke: #00ff00");
        seriesB.getNode().setStyle("-fx-stroke: #0000ff");

        stage.setTitle("Zadanie 1 BB KC!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public Image imgBin(BufferedImage img) throws IOException {

        imgr = ImageIO.read(file);
        imgb = ImageIO.read(file);
        imgg= ImageIO.read(file);

        int iRet;
        int iR, iGr, iB, iG;
        int [][] tmp = new int[width][height];
        int border = 120;
        for(int k = 0; k < 256; k++){
            histogram[k] = 0;
            histogramR[k] = 0;
            histogramG[k] = 0;
            histogramB[k] = 0;
        }

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                tmp[i][j] = img.getRGB(i, j);

                iRet = tmp[i][j];

                iB = ((int) iRet & 0xff);
                iGr = (((int) iRet & 0x00ff00) >> 8);
                iR = (((int) iRet & 0xff0000) >> 16);
                iG = ( iR + iGr + iB ) / 3;

                ++histogram[iG];
                ++histogramR[iR];
                ++histogramG[iGr];
                ++histogramB[iB];

                imgr.setRGB(i, j, (iR > border)?0xff0000:0x000000);
                imgb.setRGB(i, j, (iB > border)?0x0000ff:0x000000);
                imgg.setRGB(i, j, (iGr > border)?0x00ff00:0x000000);
                img.setRGB(i, j, (iG > border)?0xffffff:0x000000);

            }
        }
        return convertToFxImage(img);
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }

}