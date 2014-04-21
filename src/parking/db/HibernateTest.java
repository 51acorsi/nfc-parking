package parking.db;

import parking.db.Entry.EntryStatus;

public class HibernateTest {

	public static void main(String[] args) {

		// Configuration configuration = new Configuration();
		// configuration.configure("/parking/db/hibernate.cfg.xml");
		// ServiceRegistry sr= new
		// ServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
		// SessionFactory sf=configuration.buildSessionFactory(sr);

		// Configuration configuration = new
		// Configuration().configure("/parking/db/hibernate.cfg.xml");
		// StandardServiceRegistryBuilder builder = new
		// StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
		// SessionFactory sf =
		// configuration.buildSessionFactory(builder.build());
		//
		//
		// // Configuration configuration = new
		// AnnotationConfiguration().addPackage("models").addAnnotatedClass(MSISDN.class);
		// // configuration.configure("hibernate.cfg.xml");
		// // SessionFactory factory = configuration.buildSessionFactory();
		// // Session session = factory.openSession();
		//
		//
		// User user1=new User("Felipe", "felipe.01");
		// User user2=new User("Felipe 2", "felipe.02");
		//
		// Session ss = sf.openSession();
		// ss.beginTransaction();
		// //saving objects to session
		// ss.save(user1);
		// ss.save(user2);
		// ss.getTransaction().commit();
		// ss.close();

		//createUsers();
		registerUserEntry();

		HibernateSession.close();

	}
	
	private static void createUsers() {
		User user1 = new User("felipe.01", "Felipe");
		User user2 = new User("felipe.02", "Felipe 2");

		HibernateSession.getSession().beginTransaction();

		HibernateSession.getSession().save(user1);
		HibernateSession.getSession().save(user2);

		HibernateSession.getSession().getTransaction().commit();
		HibernateSession.getSession().close();

		User.findUserbyUserName("felipe.02");
	}
	
	private static void registerUserEntry() {
		User user1 = new User("felipe.01", "Felipe");
		User user2 = new User("felipe.02", "Felipe 2");

		HibernateSession.getSession().beginTransaction();

		HibernateSession.getSession().save(user1);
		HibernateSession.getSession().save(user2);

		HibernateSession.getSession().getTransaction().commit();
		HibernateSession.getSession().close();
		
		UserEntry.registerUserEntry(user1);
		user2 = User.findUserbyUserName("felipe.01");
		UserEntry ue = UserEntry.findUserEntry(user2, EntryStatus.ENTERED);
		
		if (ue != null)
		{
			System.out.println(ue.getEntryTime());
		}
	}


}
