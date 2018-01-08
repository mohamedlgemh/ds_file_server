package Distributed;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
public class Servant {
    void download(Node1 fileServer, String fileName, String path,int idd) throws RemoteException {
                
		FileSerializable fs = fileServer.downloadFile(fileName,path);
		File localFile = new File(path+ fileName);
		
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
			out.write(fs.getData(), 0, fs.getData().length);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    

 int search(Node1 fileServer, String fileName) throws RemoteException {
		int res = fileServer.searchFiles(fileName);
   
                 return res;
                 
	}  

    int check(Node1 fileserver, String fn, String path) {
          File  f= new File(path + fn);
                
		if(f.exists()) return 1;
                else return 0;
          
    }

    void upload(Node1 fileserver, String fn, String[] paths, int idd) {
       
        try {
			File f = new File(paths[idd]+ fn);
			FileSerializable fs = new FileSerializable();
			
			int fileSize = (int) f.length();
			byte[] buffer = new byte[fileSize];
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
			in.read(buffer, 0, buffer.length);
			in.close();
			
			fs.setData(buffer);
			fs.setName(fn);
			fs.setLastModifiedDate(new Date(f.lastModified()));
                        fileserver.uploadFile(fs,paths,idd);			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }

    void delete(Node1 fileserver, String fn, String p, int idd) throws RemoteException {
      
        fileserver.deleteFile(p,fn);
	System.out.println("File Deleted");
         
        
    }
    
}

 
