package repositories.pimprocesseddata;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import data.ProcessedData;

/**
* MongoDB repository for processed data.
*
* @author  Armand Maree
* @since   2016-07-16
*/
public interface PimProcessedDataRepository extends MongoRepository<ProcessedData, String> {
    public ProcessedData findByUserId(String userId);
}
