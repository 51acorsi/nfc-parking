package parking.db;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.criterion.Restrictions;

@Entity(name = "User_")
public class User {

	@Id
	@GeneratedValue(generator = "UUIDGenerator")
	@GenericGenerator(name = "UUIDGenerator", strategy = "parking.db.UUIDGenerator")
	@Column(name = "id", columnDefinition = "char(36)")
	private String id;

	@Column(name = "UserName")
	private String userName;

	@Column(name = "Name")
	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id")
	private List<UserEntry> userEntries;

	public User() {
	}

	public User(String userName, String name) {
		this.userName = userName;
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserEntry> getUserEntries() {
		return userEntries;
	}

	public void setUserEntries(List<UserEntry> userEntries) {
		this.userEntries = userEntries;
	}

	public static User findUserbyUserName(String userName) {
		try {
			Criteria criteria = HibernateSession.getSession().createCriteria(
					User.class);
			criteria.add(Restrictions.eq("userName", userName));
			criteria.setMaxResults(1);
			criteria.setFetchMode("userEntries", FetchMode.JOIN);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			User us = (User) criteria.list().get(0);
			HibernateSession.getSession().close();
			return us;
		} catch (Exception ex) {
			ex.printStackTrace();
			HibernateSession.getSession().close();
			return null;
		}
	}
}
