package repositories;

import data.ProcessedData;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
* MongoDB repository for {@link data.ProcessedData} objects.
*
* @author  Armand Maree
* @since   1.0.0
*/
public interface PimProcessedDataRepository extends MongoRepository<ProcessedData, String> {
	public ProcessedData findById(String id);
    public List<ProcessedData> findByUserId(String userId);
	public ProcessedData findByPimSourceAndPimItemId(String pimSource, String pimItemId);
}
