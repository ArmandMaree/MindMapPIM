package repositories;

import java.util.List;

import poller.FacebookPollingUser;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
* MongoDB repository for {@link poller.FacebookPollingUser}.
* <p>
*	Used to maintain information that will enable the poller to continue polling where it stopped should the service be restarted or crashes.
* </p>
*
* @author  Armand Maree
* @since 1.0.0   
*/
public interface FacebookRepository extends MongoRepository<FacebookPollingUser, String> {
	public FacebookPollingUser findByUserId(String userId);
	public List<FacebookPollingUser> findAll();
	public List<FacebookPollingUser> findByCurrentlyPolling(boolean currentlyPolling);
}