package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import poller.GmailPollingUser;

/**
* MongoDB repository for processed data.
*
* @author  Armand Maree
* @since   2016-07-24
*/
public interface GmailRepository extends MongoRepository<GmailPollingUser, String> {
	public GmailPollingUser findByUserId(String userId);
	public List<GmailPollingUser> findAll();
}
