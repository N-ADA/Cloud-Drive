import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class server{

	public static void main(String[] args) throws IOException{

		ServerSocket ss = new ServerSocket(80);//binding
		BufferedReader header_reader;
		BufferedWriter header_writer;
		DataInputStream input;
		DataOutputStream output;

		while(true){
			System.out.println("Server is waiting...");
			Socket s = ss.accept();
			System.out.println("\nServer made a connection with client "+s.getPort());
			
			header_reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			header_writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			input = new DataInputStream(s.getInputStream());
			output = new DataOutputStream(s.getOutputStream());

			try{
				String header;
				String backup = header_reader.readLine();
				File server_folder = new File(backup);
				boolean bool = server_folder.mkdir();

		  		if(bool) header = "Backup Folder created successfully!\n";
		  		else header = "Backup Folder already exists --- proceeding to check for updates\n";
		  		header_writer.write(header,0,header.length());
		  		header_writer.flush();

				File[] serverlistOfFiles = server_folder.listFiles();
				int serverfilescount = serverlistOfFiles.length;

				header = header_reader.readLine();
				int clientfilescount = Integer.parseInt(header);

				while(true){

					for(int i=0 ; i<clientfilescount ;i++){

						header = header_reader.readLine();
						StringTokenizer strk = new StringTokenizer(header," ");
						String client_file_name = strk.nextToken();
						int client_file_size = Integer.parseInt(strk.nextToken());

						//Download Files that are not yet created
						File file = new File(backup+"/"+client_file_name);
						if(!file.exists()){
							System.out.println("File : "+client_file_name+" does not exist");
							System.out.println("Downloading on progress...");
							download_file(client_file_name,client_file_size,header_writer,input,backup);

							//updates number of files in server side
							serverlistOfFiles = server_folder.listFiles();
							serverfilescount = serverlistOfFiles.length;
						}
						
						else{
							byte[] client_content = new byte[client_file_size];
							header = "Send Content\n";
							header_writer.write(header,0,header.length());
							header_writer.flush();

							if(client_file_size != -1 && client_file_name != null){
								try{
									input.readFully(client_content); //read input stream and store it in content
								}catch(EOFException e){
									System.out.println("EOF exception erorr : "+e.getMessage());
								}

								for(int j=0 ; j<serverfilescount ; j++){

									String server_file_name = serverlistOfFiles[j].getName();
									if(server_file_name.equals(".DS_Store"))continue;

									FileInputStream fileIn = new FileInputStream(backup+"/"+server_file_name);
									int file_size = fileIn.available();
									byte[] server_content =  new byte[file_size];
									fileIn.read(server_content);

									if(client_file_name.equals(server_file_name) && !Arrays.equals(server_content,client_content)){
										System.out.println("The File "+client_file_name+" has been Updated");
										FileOutputStream fileOut = new FileOutputStream(backup+"/"+server_file_name);
										fileOut.write(client_content,0,client_file_size);
										fileOut.close();
									}

								}
							}
							client_file_size = -1;
							client_file_name = null;
						}

						//updates number of current client files
						header = header_reader.readLine();
						clientfilescount = Integer.parseInt(header);
					}
					System.out.println("------Checking for Updates------");
					TimeUnit.SECONDS.sleep(4);

				}
			}catch(Exception e){
			System.out.println("error caught : "+e.getMessage());
			}


		}	
		
		
	}

	public static void download_file(String file_name, int file_size, BufferedWriter header_writer, DataInputStream input, String backup){
		try{
			byte[] content = new byte[file_size];
			String header = "Send Content\n";
			header_writer.write(header,0,header.length());
			header_writer.flush();
			if(file_size != -1 && file_name != null){
        		try{
					input.readFully(content); //read input stream and store it in content
				}catch(EOFException e){
					System.out.println("EOF exception erorr : "+e.getMessage());
				}
				FileOutputStream fileOut = new FileOutputStream(backup+"/"+file_name); //create a file with the same name under this directory
				fileOut.write(content,0,file_size);
				fileOut.close();
        	}
		}catch(Exception e){
			System.out.println("Error caught : "+e.getMessage());
		}
        file_size = -1;
        file_name = null;

	}

}