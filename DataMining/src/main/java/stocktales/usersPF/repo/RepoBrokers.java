package stocktales.usersPF.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import stocktales.usersPF.model.Broker;

@Repository
public interface RepoBrokers extends JpaRepository<Broker, String>
{
	public Optional<Broker> findByBrokercode(
	        String brokerName
	);
	
	@Query("select DISTINCT(brokercode) from Broker")
	public List<String> getBrokerNames(
	);
}
