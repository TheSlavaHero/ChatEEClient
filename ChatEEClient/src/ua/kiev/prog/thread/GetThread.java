package ua.kiev.prog.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ua.kiev.prog.JsonMessages;
import ua.kiev.prog.Main;
import ua.kiev.prog.Message;
import ua.kiev.prog.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.*;

public class GetThread implements Runnable {
    private final Gson gson;
    private int n;

    public GetThread() {
        gson = new GsonBuilder().create();
    }

    @Override
    public void run() {
        try {
            while ( ! Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get?from=" + n);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();
                try {
                    byte[] buf = responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);
                    JsonMessages list;
                    list = gson.fromJson(strBuf, JsonMessages.class);
                    Main obj = new Main();
                    final Class<?> cls = Main.class;
                    Field field = cls.getDeclaredField("login");
                    field.setAccessible(true);
                    String login = (String) field.get (obj);
                    if (list != null) {
                        for (Message m : list.getList()) {
                            if (m.getTo() == null || m.getTo().equals(login) || m.getFrom().equals(login))  {System.out.println(m);}
                            n++;
                        }
                    }
                } finally {
                    is.close();
                }
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
