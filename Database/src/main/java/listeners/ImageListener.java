package listeners;

import data.ImageDetails;
import data.ImageRequestIdentified;
import data.ImageResponseIdentified;
import data.ImageSaveRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import repositories.ImageDetailsRepository;
/**
* This class listeners for requests that require oprations on topic images.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ImageListener {
	private final String imageResponseQueueName = "image-response.frontend.rabbit";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ImageDetailsRepository imageRepository;

	/**
	* Default constructor.
	*/
	public ImageListener() {

	}

	/**
	* Receive a  request to retrieve the image details for a certain list of topics.
	* @param imageRequest The request that contains the list of objects.
	*/
	public void receiveImageRequest(ImageRequestIdentified imageRequest) {
		System.out.println("Received: " + imageRequest);
		ImageResponseIdentified imageResponse = new ImageResponseIdentified(imageRequest.getReturnId());

		for (String topic : imageRequest.getTopics()) {
			ImageDetails imageDetails;

			if (imageRequest.getSource() != null)
				imageDetails = imageRepository.findByTopicAndSource(topic, imageRequest.getSource());
			else
				imageDetails = imageRepository.findByTopic(topic);

			if (imageDetails == null)
				imageDetails = new ImageDetails(topic);

			imageResponse.addImage(imageDetails);
		}

		rabbitTemplate.convertAndSend(imageResponseQueueName, imageResponse);
		System.out.println("Responded: " + imageResponse);
	}

	public void receiveImageSave(ImageSaveRequest imageRequest) {
		System.out.println("Receive: " + imageRequest);

		for (ImageDetails imageDetails : imageRequest.getImageDetails()) {
			ImageDetails imageDetailsInRepo = imageRepository.findByTopic(imageDetails.getTopic());

			if (imageDetailsInRepo == null)
				imageDetailsInRepo = imageDetails;
			else {
				imageDetailsInRepo.setUrl(imageDetails.getUrl());
				imageDetailsInRepo.setSource(imageDetails.getSource());
			}

			imageRepository.save(imageDetailsInRepo);
			System.out.println("Saved: " + imageDetailsInRepo);
		}
	}
}