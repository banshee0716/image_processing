//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Main
{
    /**
     * 將圖片轉換為灰階格式。
     * 重構包括以下幾點：
     * 1. 使用符合 Java 命名慣例的方法名，增強可讀性。
     * 2. 简化异常处理，让调用者可以更灵活地处理异常。
     * 3. 增加清晰的注释，说明方法的用途和内部逻辑。
     *
     * @param imageName 要转换的图像的文件名（不包括扩展名）。
     * @throws IOException 如果文件读取或写入时出现错误。
     */
    public static void convertToGray(String imageName) throws IOException {
        // 读取图像文件
        File file = new File(imageName + ".jpg");
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, image.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);//從圖像中獲取特定位置的像素值。
                int a = (pixel >> 24) & 0xff;//提取alpha（透明度）通道的值。
                int r = (pixel >> 16) & 0xff;//提取紅色通道的值。
                int g = (pixel >> 8) & 0xff;//提取綠色通道的值。
                int b = pixel & 0xff;//提取藍色通道的值。

                int avg = (r + g + b) / 3; //計算紅色、綠色和藍色值的平均值。這個平均值成為了灰度值，因為在灰度圖像中，每個像素的紅、綠、藍通道值是相同的，都是該像素的灰度值。
                pixel = (a << 24) | (avg << 16) | (avg << 8) | avg; //使用alpha值和灰度值重新構造像素。
                grayImage.setRGB(x, y, pixel);
            }
        }

        // 保存灰度图像
        File output = new File(imageName + "_toGray.jpg");
        ImageIO.write(grayImage, "jpg", output);
    }
    public static void convertToNegative(String imageName) throws IOException {
        // 讀取灰階圖像文件
        File file = new File(imageName + "_toGray.jpg");
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage negativeImage = new BufferedImage(width, height, image.getType());

        // 將每個像素轉換為負片效果
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);////從圖像中獲取特定位置的像素值。

                //負片效果即為255-原始灰階值
                int r = 255 - ((pixel >> 16) & 0xff);//提取紅色值並反轉。>> 16操作將紅色值移至最低位，& 0xff操作提取這8位
                int g = 255 - ((pixel >> 8) & 0xff);//提取綠色值並反轉。同理，>> 8移動綠色值至最低位。
                int b = 255 - (pixel & 0xff);//提取藍色值並反轉。直接使用& 0xff提取最後8位。
                pixel = (r << 16) | (g << 8) | b;//重新組合反轉後的RGB值，形成新的像素值。
                negativeImage.setRGB(x, y, pixel);
            }
        }

        // 保存負片效果的圖像
        File output = new File(imageName + "_negative.jpg");
        ImageIO.write(negativeImage, "jpg", output);
    }
    public static void GammaCorrection(String imageName, float gamma) throws IOException {
        int[] maxMinValues = findMaxMin(imageName);
        int max = maxMinValues[0];
        int min = maxMinValues[1];

        File file = new File(imageName + "_toGray.jpg");
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage gammaImage = new BufferedImage(width, height, image.getType());

        // 遍歷每個像素並應用伽瑪校正
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                //取出RGB的位置
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;

                // 分別對RGB應用伽瑪校正公式：pow((color - min) / (max - min), gamma) * 255
                r = (int) Math.round(Math.pow((float) (r - min) / (max - min), gamma) * 255);
                g = (int) Math.round(Math.pow((float) (g - min) / (max - min), gamma) * 255);
                b = (int) Math.round(Math.pow((float) (b - min) / (max - min), gamma) * 255);

                pixel = (r << 16) | (g << 8) | b;//把rgb組成和設置圖片
                gammaImage.setRGB(x, y, pixel);
            }
        }

        // 根据 gamma 值保存不同的图像
        File output;
        if (gamma > 1) {
            output = new File(imageName + "_gammaBig.jpg");
        } else if (gamma < 1) {
            output = new File(imageName + "_gammaSmall.jpg");
        } else { // gamma == 1
            output = new File(imageName + "_gammaOne.jpg");
        }
        ImageIO.write(gammaImage, "jpg", output);
        /*
        String outputName = imageName + "_gammaCorrected_" + gamma + ".jpg";
        File output = new File(outputName);
        ImageIO.write(gammaImage, "jpg", output);*/
    }
    public static void addSaltAndPepperNoise(String imageName) throws IOException {
        File file = new File(imageName + "_gammaSmall.jpg");
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
        //設計一隨機數 用來隨機生成胡椒鹽
        Random rand = new Random();

        // 遍歷每個像素並隨機加入噪聲
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (rand.nextFloat() > 0.95) { // 隨機檢測，如果隨機數>0.95就添加噪音，約5%機率添加噪聲
                    int noiseColor = (rand.nextFloat() > 0.5) ? 255 : 0; // 隨機選擇黑或白
                    int newPixel = (noiseColor << 24) | (noiseColor << 16) | (noiseColor << 8) | noiseColor;
                    image.setRGB(x, y, newPixel);
                }
            }
        }

        // 輸出加入噪聲的圖片
        File output = new File(imageName + "_saltAndPepper.jpg");
        ImageIO.write(image, "jpg", output);
    }
    public static void MedianFilter(String imageName) throws IOException {
        File file = new File(imageName + "_saltAndPepper.jpg");
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, image.getType());

        // 遍歷每個像素
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                // 對於每個像素，考慮它周圍的3x3區域（包括該像素本身及其周圍的8個像素）。
                // 將這些鄰域像素的值儲存到一個陣列中。
                int[] filterArray = new int[9];
                int k = 0;
                // 獲取鄰域像素值
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        filterArray[k++] = image.getRGB(i + di, j + dj);
                    }
                }
                //sort array，之後取他的中間值，當成filter的值
                Arrays.sort(filterArray);
                int medianValue = filterArray[4];
                filteredImage.setRGB(i, j, medianValue);
            }
        }

        // 輸出應用中值濾波器後的圖片
        File output = new File(imageName + "_MedianFilter.jpg");
        ImageIO.write(filteredImage, "jpg", output);
    }
    public static void MaxFilter(String imageName, int filterSize) throws IOException {
        File file = new File(imageName + "_Laplacian.jpg");
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, image.getType());

        // 遍歷每個像素，除去邊緣
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                // 對於每個像素，考慮它周圍的3x3區域（包括該像素本身及其周圍的8個像素）。
                // 將這些鄰域像素的值儲存到一個陣列中。
                int[] filterArray = new int[9];
                int k = 0;
                // 獲取鄰域像素值
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        filterArray[k++] = image.getRGB(i + di, j + dj);
                    }
                }
                // 對鄰域像素值進行排序並取最大值用於做max filter的值
                Arrays.sort(filterArray);
                int maxValue = filterArray[8];
                filteredImage.setRGB(i, j, maxValue);
            }
        }

        // 輸出應用最大值濾波器後的圖片
        File output = new File(imageName + "_MaxFilter.jpg");
        ImageIO.write(filteredImage, "jpg", output);
    }
    public static void LaplacianFilter(String imageName, int filterSize) throws IOException {
        File file = new File(imageName + "_gammaOne.jpg");
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, image.getType());
        // 遍歷每個像素，除去邊緣
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                int[] rArr = new int[9];
                int k = 0;
                // 獲取鄰域像素值，這邊只選用紅色就是因為圖片灰度比較一致，藉此簡化計算
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        rArr[k++] = ((image.getRGB(i + di, j + dj)) >> 16) & 0xff;
                    }
                }
                // 拉普拉斯濾波計算，想一下LaplacianFilter的矩陣，套用公式
                int r = Math.abs(4 * rArr[4] - rArr[3] - rArr[1] - rArr[5] - rArr[7]);
                int newPixel = (r << 16) | (r << 8) | r;
                filteredImage.setRGB(i, j, newPixel);
            }
        }
        // 輸出應用拉普拉斯濾波器後的圖片
        File output = new File(imageName + "_Laplacian.jpg");
        ImageIO.write(filteredImage, "jpg", output);
    }
    public static void OtsuThresholding(String imageName) throws IOException {
        File file = new File(imageName + "_gammaBig.jpg");
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, image.getType());

        // 計算灰度直方圖
        int[] histogram = new int[256];
        Arrays.fill(histogram, 0);
        histogram = countgray(image);

        // 計算最佳閾值
        int threshold = chooseThreshold(histogram);

        // 遍歷每個像素進行二值化
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int r = (pixel >> 16) & 0xff;
                // 應用閾值，進行二值化
                r = (r <= threshold) ? 0 : 255;
                pixel = (r << 16) | (r << 8) | r;
                binaryImage.setRGB(x, y, pixel);
            }
        }

        // 輸出二值化後的圖像
        File output = new File(imageName + "_Otsu.jpg");
        ImageIO.write(binaryImage, "jpg", output);
    }
    public static int[] countgray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] histogram = new int[256];
        Arrays.fill(histogram, 0);

    // 統計每個灰度值的頻次
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
            // 將arbg轉為10進位，取用紅色通道
                int r = (p >> 16) & 0xff;
                histogram[r] += 1;
            }
        }
        return histogram;
    }
    public static int chooseThreshold(int[] histogram) {
        int total = Arrays.stream(histogram).sum(); // 計算圖像中的總像素數
        double maxVb = 0; // 初始化最大類間方差
        int threshold = 0; // 初始化最佳閾值
        int group1Total = 0; // 前景的像素總數
        int group1GrayTotal = 0; // 前景的灰度總和

        for (int i = 0; i < 256; i++) {
            group1GrayTotal += i * histogram[i]; // 前景灰度總和
            group1Total += histogram[i]; // 前景像素總數

            // 確保前景區域不為空
            if (group1Total == 0) continue;
            int group2Total = total - group1Total;
            // 確保背景區域不為空
            if (group2Total == 0) break;

            // 計算前景和背景的平均灰度
            double group1Mean = (double)group1GrayTotal / group1Total;
            final int tempI = i; // 创建一个临时的 final 变量
            double group2Mean = (double) IntStream.range(tempI + 1, histogram.length)
                    .mapToDouble(j -> j * histogram[j])
                    .sum() / group2Total;

            // 如果當前的類間方差大於目前找到的最大值，更新最大值和閾值
            double Vb = (double)group1Total * group2Total * Math.pow(group1Mean - group2Mean, 2);
            if (Vb > maxVb) {
                maxVb = Vb;
                threshold = i;
            }
        }
        return threshold;
    }
    public static int[] findMaxMin(String imageName) throws IOException {
        File file = new File(imageName + "_togray.jpg");
        BufferedImage image = ImageIO.read(file);
        int max = 0;
        int min = 255;

        int width = image.getWidth();
        int height = image.getHeight();
        // 更新最大和最小灰度值
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                int r = (pixel >> 16) & 0xff;
                max = Math.max(max, r);
                min = Math.min(min, r);
            }
        }
        return new int[] {max, min};
    }

    public static void main(String args[])throws IOException
    {
        String photo = "two_tree";
        float gammaSmall= 0.5f;
        float gammaBig= 2.5f;
        float gammaOne= 1.0f;
        int filter_size=3;
        convertToGray(photo);
        convertToNegative(photo);
        GammaCorrection(photo,gammaSmall);
        GammaCorrection(photo,gammaBig);
        GammaCorrection(photo,gammaOne);
        addSaltAndPepperNoise(photo);
        MedianFilter(photo);
        LaplacianFilter(photo,filter_size);
        MaxFilter(photo,filter_size);
        OtsuThresholding(photo);
    }
}