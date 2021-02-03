package ua.kiev.prog;

import ua.kiev.prog.thread.GetThread;
import ua.kiev.prog.thread.OnlineThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
	private static String login;
	private static Login fullLogin;
	private static boolean success;

	public static void main(String[] args) {

		startChat();
		startThread();
		try {
			if (success){
				System.out.println("Enter your message: ");
				checkForNewMessages();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static byte[] responseBodyToArray(InputStream is)throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[10240];
		int r;

		do {
			r = is.read(buf);
			if (r > 0) bos.write(buf, 0, r);
		} while (r != -1);

		return bos.toByteArray();
	}
	private static String checkPrivateMessage(String text) {
		char c = text.charAt(0);
		String to = null;
		if (c == '@') {
			int i = text.indexOf(' ');
			to = text.substring(1,i);
		}
		if (to != null) {
			return to;
		}
		return null;
	}

	public static Login getFullLogin() {
		return fullLogin;
	}

	public static String getLogin() {
		return login;
	}
	public static void startChat() {
		Scanner scanner = new Scanner(System.in);
		success = false;
		try {
			String password;
			URL url = new URL(Utils.getURL() + "/login");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			while (!success) {
				System.out.println("Do you want to register or login? 1 - register, 2 - login");
				String choice = scanner.nextLine();
				System.out.println("Enter your login: ");
				login = scanner.nextLine();
				System.out.println("Enter your password: ");
				password = scanner.nextLine();
				fullLogin = new Login(login, password);
				URLConnection connection = new URL(url + "?login=" + login + "&password=" + password + "&choice=" + choice).openConnection();
				try (InputStream response = connection.getInputStream()){
					byte[] buf = responseBodyToArray(response);
					String sms = new String(buf, StandardCharsets.UTF_16);
					System.out.println(sms);
					if (sms.equals("Success. You are logged in.") || sms.equals("Registration was successful.")) {
						success = true;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void startThread() {
		if (success) {
			Thread th = new Thread(new GetThread());
			th.setDaemon(true);
			th.start();

			Thread th2 = new Thread(new OnlineThread());
			th2.setDaemon(true);
			th2.start();
		}
	}
	public static void checkForNewMessages() throws IOException {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String text = scanner.nextLine();
			if (text.isEmpty()) break;
			String to = checkPrivateMessage(text);
			Message m = new Message(login, to,text);
			int res = 200;
			if (text.equals("/all")) {
				URL obj = new URL(Utils.getURL() + "/allUsers");
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoOutput(true);
				try (InputStream is = conn.getInputStream()){
					byte[] buf = responseBodyToArray(is);
					String strBuf = new String(buf, StandardCharsets.UTF_8);
					System.out.println(strBuf);
				}
			} else {
				res = m.send(Utils.getURL() + "/add");
			}
			if (res != 200) { // 200 OK
				System.out.println("HTTP error occured: " + res);
				return;
			}
		}
	}
}
