import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.InputStreamReader;

public class SendMail {
	Socket socket;
	String to;
	String from = "aamaree@gmail.com";
	String host = "smtp.vodamail.co.za";
	String username = "NTI1ODk4MTBAbXdlYi5jby56YQ==";
	String password = "aW0hOVhJIVA=";
	String name;
	int score;
	private BufferedReader in;
	private PrintWriter out;

	public static void main(String[] args) {
		SendMail sendMail = new SendMail("acubencos@gmail.com", "Acuben", 0);
		sendMail.send();
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
			out.println("HELO Armand");
			System.out.println("HELO Armand");
			String msg = in.readLine();

			while(!msg.startsWith("220 Welcome")) {
				System.out.println(msg);
				msg = in.readLine();
			}

			send("MAIL FROM: " + from);
			System.out.println("MAIL FROM: " + from);
			System.out.println(in.readLine());

			send("RCPT TO: " + to);
			System.out.println("RCPT TO: " + to);
			System.out.println(in.readLine());

			send("DATA");
			System.out.println("DATA");
			System.out.println(in.readLine());
			send("subject: Nandos advertisement");
			send("");
			send("Hi " + name + ", ");
			send("");
			send("Did you see the new advertisement by Nandos? They showed a horse, riding a motorcycle while eating a pizza. They are always so controversial.");
			
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