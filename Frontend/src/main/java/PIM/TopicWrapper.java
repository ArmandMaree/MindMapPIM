package PIM;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import data.*; 

import org.springframework.data.annotation.Id;

/**
* A user that will be saved in a database.
*
* @author  Arno Grobler
* @since   1.0.0
*/
public class TopicWrapper implements Serializable {
	private static final long serialVersionUID = 7823655319489247L;
	private String userId;
	private String topicName;
	/**
	* Indicates whether this topic is a person.
	*/
	private boolean person = false;

	/**
	* Indicates whether the user chose to hide the topic.
	*/
	private boolean hidden = false;
	/**
	* Default constructor
	*/
	public TopicWrapper() {
		super();
	}

	// /**
	// * Returns the value of person.
	// * @return Indicates whether this topic is a person.
	// */
	public boolean getPerson() {
		return person;
	}

	// /**
	// * Set the value of person.
	// * @param person Indicates whether this topic is a person.
	// */
	public void setPerson(boolean person) {
		this.person = person;
	}

	/**
	* Returns the value of hidden.
	* @return Indicates whether the user chose to hide the topic.
	*/
	public boolean getHidden() {
		return hidden;
	}

	// /**
	// * Set the value of hidden.
	// * @param hidden Indicates whether the user chose to hide the topic.
	// */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	/**
	* Returns the value of hidden.
	* @return Indicates whether the user chose to hide the topic.
	*/
	public String getUserId() {
		return userId;
	}

	// /**
	// * Set the value of hidden.
	// * @param hidden Indicates whether the user chose to hide the topic.
	// */
	public void setUserId(String userId) {
		this.userId = userId;
	}
		/**
	* Returns the value of hidden.
	* @return Indicates whether the user chose to hide the topic.
	*/
	public String getTopicName() {
		return topicName;
	}

	// /**
	// * Set the value of hidden.
	// * @param hidden Indicates whether the user chose to hide the topic.
	// */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	/**
	* Returns a string representation of a user used for printing.
	* @return User as a string.
	*/
	@Override
	public String toString() {
		return "TopicWrapper {\n"+
			"\tuserId:" + userId + "\n" +
			"\ttopicName:" + topicName + "\n" +
			"\thidden:" + hidden + "\n" +
			"\tperson:" + person + "\n" +
		"}";
	}
}
