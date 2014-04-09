package parking.db;

import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Column;  
import javax.persistence.Entity;  
import javax.persistence.GeneratedValue;  
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name="User")
public class User {
	
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "seq_user")
	@SequenceGenerator(name="seq_user", sequenceName="seq_user")
	private Long id;
	
	@Column(name="User_Id")  
	private String userId;
	
	@Column(name="Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

}
