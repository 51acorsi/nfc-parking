package parking.db;

import javax.imageio.spi.ServiceRegistry;

import org.hibernate.Session;  
import org.hibernate.SessionFactory;  
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;  

public class HibernateTest {
	
	 public static void main(String[] args) {  
	      
//		  Configuration configuration = new Configuration();
//		  configuration.configure("/parking/db/hibernate.cfg.xml");		  
//		  ServiceRegistry sr= new ServiceRegistryBuilder().applySettings(configuration.getProperties()).build();  
//		  SessionFactory sf=configuration.buildSessionFactory(sr);
		  
		  Configuration configuration = new Configuration().configure("/parking/db/hibernate.cfg.xml");
		  StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
		  SessionFactory sf = configuration.buildSessionFactory(builder.build());
		  
		  
//		  Configuration configuration = new AnnotationConfiguration().addPackage("models").addAnnotatedClass(MSISDN.class);
//		  configuration.configure("hibernate.cfg.xml");
//		  SessionFactory factory = configuration.buildSessionFactory();
//		  Session session = factory.openSession();
		  
		  
		  User user1=new User("Felipe");		    
		  User user2=new User("Felipe 2");  
 
		  Session ss = sf.openSession();  
		  ss.beginTransaction();  
		 //saving objects to session  
		  ss.save(user1);  
		  ss.save(user2);  
		  ss.getTransaction().commit();  
		  ss.close();  
		    
		 }  
	
}
