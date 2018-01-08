package Distributed;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.swing.JOptionPane;

public class Main {
	
	public static void main(String[] args){
        
		int  idx = Integer.parseInt(args[0]);
                        
   
                
		
		try {
			
			Node obj = new Node(idx);
			initServer(obj);
			initClient(obj, idx);
			
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		} catch (NotBoundException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void initServer(Node obj) throws RemoteException{
		//communication on port
                Registry reg = LocateRegistry.createRegistry(obj.myPort); 
		reg.rebind(obj.myService, obj);
	}
	
	public static void initClient(final Node obj, int idx) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(obj.myIp,obj.myPort); 
	        final Node1 reg = (Node1)registry.lookup(obj.myService);
		Timer timer = new Timer();   
                //start with new req
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    obj.fetchNewMessages(reg);
                } catch (RemoteException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, 0, 100);
		
		while (true){
                    String title=JOptionPane.showInputDialog("Enter the Operation download - upload - search - delete");
                    
                    String msg = title ;
                    
                    String title2=JOptionPane.showInputDialog("Enter filename");
                    String fn = title2 ;
                    
                       
			String Id = UUID.randomUUID().toString();
			String sender = obj.myService;
                        obj.lClock++;
			Message m = new Message(Id, sender, msg, obj.lClock,fn);  
			obj.multicastMessage(m);
		}
                
		
	}
	
}