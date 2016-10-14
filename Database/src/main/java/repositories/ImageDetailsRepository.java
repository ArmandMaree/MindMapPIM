package repositories;

import data.ImageDetails;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
* MongoDB repository for {@link data.ImageDetails} objects.
*
* @author  Armand Maree
* @since   1.0.0
*/
public interface ImageDetailsRepository extends MongoRepository<ImageDetails, String> {
	public ImageDetails findByTopicAndSource(String topic, String source);
	public ImageDetails findByTopic(String topic);
}
