package repositories;

import data.Topic;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


/**
* MongoDB repository for {@link data.Topic} objects.
*
* @author  Armand Maree
* @since   1.0.0
*/
public interface TopicRepository extends MongoRepository<Topic, String> {
	public Topic findByTopicAndUserId(String topic, String userId);
	public Topic findByTopicAndUserIdAndHidden(String topic, String userId, boolean hidden);
	public List<Topic> findByUserIdAndPerson(String userId, boolean person);
	public List<Topic> findByUserId(String userId);
	public List<Topic> findByUserIdAndHidden(String userId, boolean hidden);
}
