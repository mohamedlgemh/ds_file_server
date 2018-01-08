package Distributed ;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node1 extends Remote{
	String[] ipAddr = {"localhost", "localhost","localhost","localhost","localhost"};
        String[] services = {"A", "B" ,"C", "D","E"};
        Integer [] id = {0,1,2,3,4};
	Integer[] ports = {2000, 3000,4000, 5000, 6000};
	String [] paths = {"C:\\x\\Node1\\","C:\\x\\Node2\\","C:\\x\\Node3\\","C:\\x\\Node4\\","C:\\x\\Node5\\"};
	
	void receiveMessage(Message m) throws RemoteException, NotBoundException;
	void receiveAck(Message m) throws RemoteException;
        int searchFiles(String fileName) throws RemoteException;
        FileSerializable downloadFile(String fileName, String path)throws RemoteException;
        void uploadFile(FileSerializable f,String []path,int add) throws RemoteException;
        boolean deleteFile(String path,String fileName) throws RemoteException;
}
