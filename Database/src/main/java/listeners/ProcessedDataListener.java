package listeners;

import data.ProcessedData;
import repositories.pimprocesseddata.PimProcessedDataRepository;
import repositories.user.*;

/**
* Receives processed data from a queue messaging applicatiion and persists it.
*
* @author  Armand Maree
* @since   2016-07-16
*/
public class ProcessedDataListener {
	private PimProcessedDataRepository processedDataRepository;
	private UserRepository userRepository;

	/**
	* Default constructor and initializes some variables.
	* @param processedDataRepository The repository where the processed data will be persisted.
	* @param userRepository The repository where user information is persisted.
	*/
	public ProcessedDataListener(PimProcessedDataRepository processedDataRepository, UserRepository userRepository) {
		this.processedDataRepository = processedDataRepository;
		this.userRepository = userRepository;
	}

	/**
	* Receives processedData and updates the userId then sends the object to the repositry for persistence.
	* @param processedData The object that needs to be persisted.
	*/
	public void receiveProcessedData(ProcessedData processedData) {
		try {
			switch (processedData.getPimSource()) {
				case "Gmail":
					User user = userRepository.findByGmailId(processedData.getUserId());

					if (user == null)
						break;

					processedData.setUserId(user.getUserId());
					System.out.println("UID: " + user.getUserId());
					break;
				default:
					break;
			}

			processedDataRepository.save(processedData);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
