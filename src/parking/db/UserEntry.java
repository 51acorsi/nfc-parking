package parking.db;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import parking.db.Entry.EntryStatus;

@Entity(name = "UserEntry")
@DiscriminatorValue("UserEntry")
public class UserEntry extends Entry {

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public UserEntry() {
	}

	public UserEntry(Date enteredOn, User user) {
		super(enteredOn);
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	//Inserts
	public static UserEntry registerUserEntry(User user) {
		UserEntry uEntry = new UserEntry(new Date(), user);

		HibernateSession.getSession().beginTransaction();

		HibernateSession.getSession().save(uEntry);

		HibernateSession.getSession().getTransaction().commit();
		HibernateSession.getSession().close();
		
		return uEntry;
	}
	
	//Queries
	public static UserEntry findUserEntry(User user, EntryStatus status) {
		try {
			Criteria criteria = HibernateSession.getSession().createCriteria(UserEntry.class);
			criteria.add(Restrictions.eq("status", status));
			criteria.createCriteria("user").add(Restrictions.eq("id", user.getId()));
			criteria.setFetchMode("user", FetchMode.JOIN);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			criteria.setMaxResults(1);
			UserEntry uEntry = (UserEntry) criteria.list().get(0);
			HibernateSession.getSession().close();
			return uEntry;
		} catch (Exception ex) {
			ex.printStackTrace();
			HibernateSession.getSession().close();
			return null;
		}
	}
	
}
