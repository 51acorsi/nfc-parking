package parking.db;

import javax.persistence.Column;  
import javax.persistence.Entity;  
import javax.persistence.GeneratedValue;  
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="User_")
public class User {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator="UUIDGenerator")
    @GenericGenerator(name="UUIDGenerator", strategy="parking.db.UUIDGenerator")
	@Column(name = "id", columnDefinition = "char(36)")
	private String id;
	
	@Column(name="Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User(String name)
	{
		this.name = name;
	}
	
}
