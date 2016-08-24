package repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import data.*;

/**
* MongoDB repository for {@link data.User} objects.
*
* @author  Armand Maree
* @since   1.0.0
*/
public interface UserRepository extends MongoRepository<User, String> {
    public List<User> findAll();
    public User findByUserId(String userId);
    public List<User> findByFirstName(String firstName);
    public List<User> findByLastName(String lastName);
	public User findByGmailId(String gmailId);
}
