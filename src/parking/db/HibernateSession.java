package parking.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSession {
	
	private static Session currentSession;
	private static SessionFactory currentSessionFactory;
	
	public static Session getSession()
	{
		if (currentSession == null)
		{
		  Configuration configuration = new Configuration().configure("/parking/db/hibernate.cfg.xml");
		  StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
		  currentSessionFactory = configuration.buildSessionFactory(builder.build());		  
		  
		  currentSession = currentSessionFactory.openSession();  		  
		} 
		else if (!currentSession.isOpen())
		{
			currentSession = currentSessionFactory.openSession();
		}
		
		return currentSession;		
	}
	
	public static void close()
	{
		currentSessionFactory.close();
	}
}
