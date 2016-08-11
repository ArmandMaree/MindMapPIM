package repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import data.*;

/**
* MongoDB repository for user information.
*
* @author  Armand Maree
* @since   2016-07-16
*/
public interface UserRepository extends MongoRepository<User, String> {
    public List<User> findAll();
    public User findByUserId(String userId);
    public List<User> findByFirstName(String firstName);
    public List<User> findByLastName(String lastName);
	public User findByGmailId(String gmailId);
}
