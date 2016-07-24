package listeners;

import java.util.*;
import repositories.user.*;
import repositories.pimprocesseddata.*;
import repositories.topic.*;

import repositories.pimprocesseddata.PimProcessedDataRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
* Receives topic requests from a queue messaging application and responds to it.
*
* @author  Armand Maree
* @since   2016-07-24
*/
public class TopicListener {
	private final String topicResponseQueueName = "topic-response.business.rabbit";
	private RabbitTemplate rabbitTemplate;
	private UserRepository userRepository;
	private PimProcessedDataRepository processedDataRepository;
	private TopicRepository topicRepository;

	/**
	* Default constructor.
	* @param rabbitTemplate Used to send messages via a RabbitMQ messaging service.
	* @param processedDataRepository The repository where the processed data will be persisted.
	*/
	public TopicListener(RabbitTemplate rabbitTemplate, UserRepository userRepository, PimProcessedDataRepository processedDataRepository, TopicRepository topicRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.userRepository = userRepository;
		this.processedDataRepository = processedDataRepository;
		this.topicRepository = topicRepository;
	}

	public void receiveTopicRequest(TopicRequest topicRequest) {
		// debug
		if (topicRequest.getUserId() == null)
			topicRequest.setUserId(userRepository.findByGmailId("acubencos@gmail.com").getUserId());
		// debug end
		String[] returnTopics = null;

		if (topicRequest.getPath() == null || topicRequest.getPath().length == 0) {
			List<Topic> topics = topicRepository.findByUserId(topicRequest.getUserId());
			returnTopics = new String[topics.size()];

			for (int i = 0; i < topics.size(); i++)
				returnTopics[i] = topics.get(i).getTopic();
		}
		else {
			for (int i = 0; i < topicRequest.getPath().length; i++) {
				String pathTopic = topicRequest.getPath()[i];
				Topic topic = topicRepository.findByTopicAndUserId(pathTopic, topicRequest.getUserId());

				if (topic == null)
					break;

				ArrayList<String> relatedTopics = new ArrayList<>(Arrays.asList(topic.getRelatedTopics()));

				if (i == topicRequest.getPath().length - 1) {
					if (topicRequest.getExclude() != null && topicRequest.getExclude().length != 0)
						relatedTopics.removeAll(Arrays.asList(topicRequest.getExclude()));

					relatedTopics.removeAll(Arrays.asList(topicRequest.getPath()));

					if (relatedTopics.size() != 0)
						returnTopics = relatedTopics.toArray(new String[0]);

					break;
				}

				if (!relatedTopics.contains(topicRequest.getPath()[i + 1]))
					break;
			}
		}

		TopicResponse topicResponse = new TopicResponse(topicRequest.getUserId(), returnTopics);
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
	}
}
