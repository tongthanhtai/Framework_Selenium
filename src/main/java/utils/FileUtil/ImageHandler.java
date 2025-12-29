package utils.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageHandler {

    public static void fullScreenCapture() throws AWTException, IOException {
        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rectangle = new Rectangle(resolution);
        Robot robot = new Robot();
        BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
        Graphics g = bufferedImage.getGraphics();
        File out = new File("src/output/capturer/testcapturer.png");
        ImageIO.write(bufferedImage, "png", out);
    }
}
