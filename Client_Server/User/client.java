import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Scanner;

public class client{
	public static void main(String[] args) {

		String client_folder_name = args[0];
		File client_folder = new File(client_folder_name);
		File[] clientlistOfFiles = client_folder.listFiles();
		int clientfilescount = clientlistOfFiles.length;
		Scanner sc = new Scanner(System.in);

		String header,file_name,backup;
		int file_size;
		byte[] content;

		try{

			Socket s = new Socket("localhost",80);
			System.out.println(client_folder_name+" connected to the cloud server");

			BufferedReader header_reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter header_writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

			DataInputStream input = new DataInputStream(s.getInputStream());
			DataOutputStream output = new DataOutputStream(s.getOutputStream());

			System.out.println("Give name to your backup folder : ");
			backup = sc.next();

			header = backup+"\n";
			header_writer.write(header,0,header.length());
			header_writer.flush();

			header = header_reader.readLine();
			System.out.println(header);

			header = clientfilescount+"\n";
			header_writer.write(header,0,header.length());
			header_writer.flush();

			

			while(true){// keeps sending files for upload or update

				for(int i=0 ; i<clientfilescount ; i++){

					file_name = clientlistOfFiles[i].getName();
					if(file_name.equals(".DS_Store"))continue;
					
					FileInputStream fileIn = new FileInputStream(client_folder_name+"/"+file_name);
					file_size = fileIn.available();

					header = file_name+" "+file_size+"\n";
					header_writer.write(header,0,header.length());
					header_writer.flush();

					header = header_reader.readLine();
					if(header.equals("Send Content")){
						content = new byte[file_size];
						fileIn.read(content);
						fileIn.close();
						output.write(content,0, file_size);
					}

					clientlistOfFiles = client_folder.listFiles();
					clientfilescount = clientlistOfFiles.length;

					header = clientfilescount+"\n";
					header_writer.write(header,0,header.length());
					header_writer.flush();

				}
			}


		}catch(Exception e){
			e.printStackTrace();
		}

	}

}