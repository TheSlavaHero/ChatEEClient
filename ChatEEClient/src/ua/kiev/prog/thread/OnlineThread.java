package ua.kiev.prog.thread;

import ua.kiev.prog.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static ua.kiev.prog.Main.getFullLogin;

public class OnlineThread implements Runnable {
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {

                URL url = new URL(Utils.getURL() + "/OnlineUser");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);

                getFullLogin().setTime();
                String text = getFullLogin().toJSON();
                byte[] out = text.getBytes(StandardCharsets.UTF_8);
                int length = out.length;

                http.setFixedLengthStreamingMode(length);
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                http.connect();
                try (OutputStream os = http.getOutputStream()) {
                    os.write(out);
                }
                Thread.sleep(1000);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}



