package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import poller.FacebookPollingUser;

/**
* MongoDB repository for processed data.
*
* @author  Armand Maree
* @since   2016-07-24
*/
public interface FacebookRepository extends MongoRepository<FacebookPollingUser, String> {
	public FacebookPollingUser findByUserId(String userId);
	public List<FacebookPollingUser> findAll();
	public List<FacebookPollingUser> findByCurrentlyPolling(boolean currentlyPolling);
}