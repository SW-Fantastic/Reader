package org.swdc.reader;

import org.swdc.fx.FXResources;
import org.swdc.fx.SwingSplashView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 展示启动的闪屏画面
 * @author SW-Fantastic
 */
public class SplashScreen extends SwingSplashView {

    private JWindow splash;

    public SplashScreen(FXResources resources) {
        super(resources);
    }

    @Override
    public JWindow getSplash() {
        if (splash != null) {
            return splash;
        }
        JWindow window = new JWindow();
        window.setBackground(new Color(0,0,0,0));
        ImageIcon image = new ImageIcon();

        try (InputStream in = new FileInputStream(resources.getAssetsFolder().getAbsolutePath() + File.separator +"splash.png")){
            BufferedImage splashImage = ImageIO.read(in);
            image.setImage(splashImage.getScaledInstance((int)(splashImage.getWidth() / 1.5), (int)(splashImage.getHeight() / 1.5),Image.SCALE_SMOOTH));
            JLabel imgLab = new JLabel(image) {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    super.paint(g2d);
                }
            };
            window.setContentPane(imgLab);
            window.setSize(image.getIconWidth(),image.getIconHeight());
            window.setLocationRelativeTo(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.splash = window;
        return window;
    }

}
