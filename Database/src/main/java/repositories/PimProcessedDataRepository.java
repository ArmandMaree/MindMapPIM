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
public interface PimProcessedDataRepository extends MongoRepository<ProcessedData, String> {
	public ProcessedData findById(String id);
    public List<ProcessedData> findByUserId(String userId);
	public ProcessedData findByPimSourceAndPimItemId(String pimSource, String pimItemId);
}
