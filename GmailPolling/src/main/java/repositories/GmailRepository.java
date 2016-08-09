package repositories;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import data.PollingUser;

/**
* MongoDB repository for processed data.
*
* @author  Armand Maree
* @since   2016-07-24
*/
public interface GmailRepository extends MongoRepository<PollingUser, String> {
	public PollingUser findByUserId(String userId);
	public List<PollingUser> findAll();
}
