package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import poller.TwitterPollingUser;

/**
* MongoDB repository for {@link poller.TwitterPollingUser}.
* <p>
*	Used to maintain information that will enable the poller to continue polling where it stopped should the service be restarted or crashes.
* </p>
*
* @author  Armand Maree
* @since 1.0.0   
*/
public interface TwitterRepository extends MongoRepository<TwitterPollingUser, String> {
	public TwitterPollingUser findByUserId(String userId);
	public List<TwitterPollingUser> findAll();
	public List<TwitterPollingUser> findByCurrentlyPolling(boolean currentlyPolling);
}