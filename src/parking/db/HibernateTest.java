package parking.db;

import org.hibernate.Session;  
import org.hibernate.SessionFactory;  
import org.hibernate.cfg.Configuration;  
import org.hibernate.service.ServiceRegistry;  
import org.hibernate.service.ServiceRegistryBuilder; 

public class HibernateTest {
	
	 public static void main(String[] args) {  
	      
		  Configuration configuration=new Configuration();  
		  configuration.configure();  
		  ServiceRegistry sr= new ServiceRegistryBuilder().applySettings(configuration.getProperties()).build();  
		  SessionFactory sf=configuration.buildSessionFactory(sr);  
		    
		  User user1=new User();  

		    
		  User user2=new User();  
 
		  Session ss=sf.openSession();  
		  ss.beginTransaction();  
		 //saving objects to session  
		  ss.save(user1);  
		  ss.save(user2);  
		  ss.getTransaction().commit();  
		  ss.close();  
		    
		 }  
	
}
