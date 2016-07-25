package repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import data.User;

/**
* MongoDB repository for user information.
*
* @author  Armand Maree
* @since   2016-07-16
*/
public interface UserRepository extends MongoRepository<User, String> {
    public User findByUserId(String userId);
    public List<User> findByFirstName(String firstName);
    public List<User> findByLastName(String lastName);
	public User findByGmailId(String gmailId);
}
