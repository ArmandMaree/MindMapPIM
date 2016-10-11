package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import poller.TwitterPollingUser;

/**
* MongoDB repository for processed data.
*
* @author  Armand Maree
* @since   2016-07-24
*/
public interface TwitterRepository extends MongoRepository<TwitterPollingUser, String> {
	public TwitterPollingUser findByUserId(String userId);
	public List<TwitterPollingUser> findAll();
	public List<TwitterPollingUser> findByCurrentlyPolling(boolean currentlyPolling);
}