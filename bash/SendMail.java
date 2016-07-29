import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.InputStreamReader;

public class SendMail {
	Socket socket;
	String to;
	String from = "acubencos@gmx.com";
	String host = "mail.gmx.com";
	String username = "YWN1YmVuY29zQGdteC5jb20=";
	String password = "YWN1YmVuY29zMTIzNA==";
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
			socket = new Socket(host, 587);
			System.out.println("Connection success");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			String msg = in.readLine();
			while(!msg.startsWith("220 gmx.com")) {
				System.out.println("NOPE: " + msg);
				msg = in.readLine();
			}
			System.out.println(msg);
			Thread.sleep(2000);
			out.println("EHLO " + host);
			System.out.println("EHLO " + host);
			msg = in.readLine();
			System.out.println(msg);

			while(!msg.startsWith("250-gmx.com")) {
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

			send("MAIL FROM:<" + from + ">");
			System.out.println("MAIL FROM:<" + from + ">");
			System.out.println(in.readLine());

			send("RCPT TO:<" + to + ">");
			System.out.println("RCPT TO:<" + to + ">");
			System.out.println(in.readLine());

			send("DATA");
			System.out.println("DATA");
			System.out.println(in.readLine());
			send("subject: Quizzer Result");
			send("");
			send("Hi " + name + ", ");
			send("");
			send("The pizza we had yesterday was really nice. Maybe we should get coffee next time.");

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
		catch(InterruptedException ie){return false;}
	}

	void send(String message) throws IOException {
		out.print(message + "\r\n");
		out.flush();
	}
}
