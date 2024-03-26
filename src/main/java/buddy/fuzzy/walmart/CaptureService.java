package buddy.fuzzy.walmart;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;

import static buddy.fuzzy.walmart.Main.misskeyClient;

@SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
public class CaptureService extends Thread {
    public void run() {
        Webcam webcam = Webcam.getDefault();
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setImageSizeDisplayed(true);

        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();

        JFrame window = new JFrame("Webcam");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);

        try {
            while (true) {
                misskeyClient.makeCaptureNote();
                Thread.sleep(3600000);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
