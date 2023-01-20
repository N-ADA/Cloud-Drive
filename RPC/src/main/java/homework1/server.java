package homework1;

import javax.jws.WebService;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@WebService
public class server {
    String folder_name;
    File server_folder;

    public void create_backup_folder(String backup_folder){
        folder_name = "src/main/java/homework1/"+backup_folder;
        server_folder = new File(folder_name);
        boolean bool = server_folder.mkdir();

        if (bool) System.out.println("---Backup Folder created successfully !---\n");
        else System.out.println("---Backup Folder already exists !---");
    }

    public void sync(String file_name, int file_size, byte[] content){
        File file = new File(folder_name + "/" + file_name);
        if(!file.exists()){
            upload(file_name, file_size, content);
        }
        else{
            System.out.println("---Checking for Updates---");
            String server_file_name;
            File[] server_files = server_folder.listFiles();
            int files_count = server_files.length;

            for(int i=0 ; i<files_count ;i++) {
                server_file_name = server_files[i].getName();
                if (file_name.equals(server_file_name)) {
                    FileInputStream fileIn = null;
                    try {
                        fileIn = new FileInputStream(folder_name + "/" + server_file_name);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    int server_file_size = 0;
                    try {
                        server_file_size = fileIn.available();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    byte[] server_content = new byte[server_file_size];
                    try {
                        fileIn.read(server_content);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if(!Arrays.equals(server_content, content)){
                        FileOutputStream fileOut = null;
                        try {
                            fileOut = new FileOutputStream(folder_name + "/"+ file_name);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            fileOut.write(content, 0, file_size);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            fileOut.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println("The File " + file_name + " has been Updated");
                    }

                }
            }
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void upload(String file_name, int file_size, byte[] content){
            System.out.println("Downloading "+file_name+" on progress...");
            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(folder_name+"/"+file_name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fileOut.write(content,0,file_size);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}