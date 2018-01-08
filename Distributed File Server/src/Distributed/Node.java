package Distributed;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Node extends UnicastRemoteObject implements Node1 {

    private static final long serialVersionUID = 1L;
    Message x;
    final static int n = ipAddr.length;
    int h;
    int lClock, myPort;
    String myIp, myService, path;
    PriorityQueue<Message> reqs;
    HashMap<String, Integer> messageAcks;
    Scanner scan;
    int idd;
    int m; 

    protected Node(int idx) throws RemoteException {
        myIp = ipAddr[idx];
        myPort = ports[idx];
        myService = services[idx];
        path = paths[idx];
        lClock = 0;
        reqs = new PriorityQueue<Message>();
        messageAcks = new HashMap<String, Integer>();
       // scan = new Scanner(System.in);
        idd = id[idx];
    }

    public void multicastMessage(Message m) throws RemoteException, NotBoundException {
        for (int i = 0; i < n; i++) {
            Registry reg = LocateRegistry.getRegistry(ports[i]);
            Node1 e = (Node1) reg.lookup(services[i]);
            e.receiveMessage(m);
        }
    }

    @Override
    public void receiveMessage(Message m) throws RemoteException, NotBoundException {
        reqs.add(m);
        if (!m.sender.equalsIgnoreCase(myService)) {
            lClock = Math.max(lClock, m.lClock) + 1;
        }
        multicastAck(m);
    }

    private void multicastAck(Message m) throws RemoteException, NotBoundException {
        for (int i = 0; i < n; i++) {
            Registry reg = LocateRegistry.getRegistry(ports[i]);
            Node1 e = (Node1) reg.lookup(services[i]);
            e.receiveAck(m);
        }
    }

    @Override
    public void receiveAck(Message m) throws RemoteException {
        if (messageAcks.containsKey(m.Id)) {
            messageAcks.put(m.Id, messageAcks.get(m.Id) + 1);
            
        } else {
            messageAcks.put(m.Id, 1);
        }      
    }

    public void fetchNewMessages(final Node1 fileserver) throws RemoteException {
        String p;
        if (reqs.size() > 0 && messageAcks.get(reqs.peek().Id) == n) {
            x = reqs.poll();
            
            if (x.req.equals("search")) {
                Servant servant = new Servant();
                 h = servant.search(fileserver, x.fn);
          
                
            }
            

            if (x.req.equals("download")) {

                Servant servant = new Servant();
                h = servant.search(fileserver, x.fn);
                if (h == -1) {
                    System.out.println("File is not founddd");
                } else {
                    System.out.println("the sender" + x.sender);

                    if (x.sender.equals("A")) {
                        p = Node1.paths[0];
                    } else if (x.sender.equals("B")) {
                        p = Node1.paths[1];
                    } else if (x.sender.equals("C")) {
                        p = Node1.paths[2];
                    } else if (x.sender.equals("D")) {
                        p = Node1.paths[3];
                    } else {
                        p = Node1.paths[4];
                    }

                    servant.download(fileserver, x.fn, p, idd);
                }

            } else if (x.req.equals("upload")) {
                Servant servant = new Servant();
                if (x.sender.equals("A")) {
                    p = Node1.paths[0];
                } else if (x.sender.equals("B")) {
                    p = Node1.paths[1];
                } else if (x.sender.equals("C")) {
                    p = Node1.paths[2];
                } else if (x.sender.equals("D")) {
                    p = Node1.paths[3];
                } else {
                    p = Node1.paths[4];
                }

                m = servant.check(fileserver, x.fn, p);
                if (m == 0) {
                    System.out.println("File is not found");
                } else {
                    servant.upload(fileserver, x.fn, Node1.paths, idd);

                }

            } else if (x.req.equals("delete")) {

                Servant servant = new Servant();
                h = servant.search(fileserver, x.fn);
                if (h == -1) {
                    System.out.println("File is not founddd");
                } else {
                    System.out.println("the sender" + x.sender);
                    if (x.sender.equals("A")) {
                        p = Node1.paths[0];
                    } else if (x.sender.equals("B")) {
                        p = Node1.paths[1];
                    } else if (x.sender.equals("C")) {
                        p = Node1.paths[2];
                    } else if (x.sender.equals("D")) {
                        p = Node1.paths[3];
                    } else {
                        p = Node1.paths[4];
                    }

                    servant.delete(fileserver, x.fn, p, idd);
                }

            } 
            

        }
        

    }
    

    @Override
    public FileSerializable downloadFile(String fileName, String path) {
        System.out.println("Downloading File...");
        try {
            File f = new File(path + fileName);
            FileSerializable fs = new FileSerializable();

            int fileSize = (int) f.length();
            byte[] buffer = new byte[fileSize];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            in.read(buffer, 0, buffer.length);
            in.close();

            fs.setData(buffer);
            fs.setName(fileName);
            fs.setLastModifiedDate(new Date(f.lastModified()));

            return fs;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int searchFiles(String fileName) throws RemoteException {

        System.out.println("Searching for File...");
        File f;
        for (int i = 0; i < 5; i++) {
            f = new File(Node1.paths[i] + fileName);

            if (f.exists()) {
            System.out.println("File is found");
                
            }
            if(!f.exists()){
                 System.out.println("File is not found");

            }
            return i;
            
            
        }
        return -1;

    }

    public void uploadFile(FileSerializable fs, String[] paths, int idd) throws RemoteException {

        File localFile;
        for (int i = 0; i < 5; i++) {
            if (i != idd) {
                localFile = new File(paths[i] + fs.getName());

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
        }
    }

    public boolean deleteFile(String path, String fileName) throws RemoteException {
        //System.out.println(path);
        File f = new File(path + fileName);
        return f.delete();
    }


}
