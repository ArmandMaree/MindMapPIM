package repositories;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import data.*;

/**
* MongoDB repository for processed data.
*
* @author  Armand Maree
* @since   2016-07-24
*/
public interface TopicRepository extends MongoRepository<Topic, String> {
	public Topic findByTopicAndUserId(String topic, String userId);
	public List<Topic> findByUserIdAndPerson(String userId, Boolean person);
	public List<Topic> findByUserId(String userId);
}
