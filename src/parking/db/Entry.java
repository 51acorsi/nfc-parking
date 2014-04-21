package parking.db;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "Entry")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "EntryType", discriminatorType = DiscriminatorType.STRING)
public class Entry {

	public enum EntryStatus {
		ENTERED, 
		COMPLETED;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private int id;

	@Enumerated(EnumType.ORDINAL)
	private EntryStatus status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date entryTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date exitTime;

	public Entry() {
	}

	public Entry(Date entryTime) {
		this.status = EntryStatus.ENTERED;
		this.entryTime = entryTime;
	}

	public EntryStatus getStatus() {
		return status;
	}

	public void setStatus(EntryStatus status) {
		this.status = status;
	}

	public Date getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}

	public Date getExitTime() {
		return exitTime;
	}

	public void setExitTime(Date exitTime) {
		this.exitTime = exitTime;
	}
}
