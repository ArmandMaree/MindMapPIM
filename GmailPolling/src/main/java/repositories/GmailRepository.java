package repositories;

import java.util.List;

import poller.GmailPollingUser;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
* MongoDB repository for {@link poller.GmailPollingUser}.
*
* @author  Armand Maree
* @since   1.0.0
*/
public interface GmailRepository extends MongoRepository<GmailPollingUser, String> {
	public GmailPollingUser findByUserId(String userId);
	public List<GmailPollingUser> findAll();
	public List<GmailPollingUser> findByCurrentlyPolling(boolean currentlyPolling);
}
