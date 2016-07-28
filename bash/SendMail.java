import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.InputStreamReader;

public class SendMail {
	Socket socket;
	String to;
	String from = "codehaven@mweb.co.za";
	String host = "smtp.mweb.co.za";
	String username = "NTI1ODk4MTBAbXdlYi5jby56YQ==";
	String password = "aW0hOVhJIVA=";
	String name;
	int score;
	private BufferedReader in;
	private PrintWriter out;

	public static void main(String[] args) {
		SendMail sendMail = new SendMail("acubencos@gmail.com", "Acuben", 0);
	}

	public SendMail(String to, String name, int score) {
		this.to = to;
		this.name = name;
		this.score = score;
	}

	public boolean send()
	{
		try{
			socket = new Socket(host, 25);
			System.out.println("Connection success");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("EHLO " + host);
			System.out.println("EHLO " + host);
			String msg = in.readLine();

			while(!msg.equals("250 HELP")) {
				System.out.println(msg);
				msg = in.readLine();
			}

			System.out.println(msg);
			send("AUTH LOGIN");
			System.out.println("AUTH LOGIN");
			System.out.println(in.readLine());
			send(username);
			System.out.println(username);
			System.out.println(in.readLine());
			send(password);
			System.out.println(password);
			System.out.println(in.readLine());

			send("MAIL FROM: " + from);
			System.out.println("MAIL FROM: " + from);
			System.out.println(in.readLine());

			send("RCPT TO: " + to + " ");
			System.out.println("RCPT TO: " + to + " ");
			System.out.println(in.readLine());

			send("DATA");
			System.out.println("DATA");
			System.out.println(in.readLine());
			send("subject: Quizzer Result");
			send("");
			send("Hi " + name + ", ");
			send("");
			send("Can we eat pizza after we rode horse tomorrow?");
			
			out.flush();
			send(".");
			System.out.println(".");
			System.out.println(in.readLine());

			send("QUIT");
			System.out.println(in.readLine());
			in.close();
			out.close();
			socket.close();
			return true;
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
	}

	void send(String message) throws IOException {
		out.println(message);
		out.flush();
	}
}