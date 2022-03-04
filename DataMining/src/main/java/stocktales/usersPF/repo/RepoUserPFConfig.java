package stocktales.usersPF.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.usersPF.model.UserPFConfig;

@Repository
public interface RepoUserPFConfig extends JpaRepository<UserPFConfig, Long>
{
	
	public Optional<UserPFConfig> findByUsername(
	        String userName
	);
}
