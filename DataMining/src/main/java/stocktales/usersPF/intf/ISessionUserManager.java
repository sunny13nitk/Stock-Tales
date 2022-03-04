package stocktales.usersPF.intf;

import java.util.List;

import stocktales.usersPF.helperPojo.PFBalDepSummary;
import stocktales.usersPF.helperPojo.UserStrategySnaphot;
import stocktales.usersPF.model.UserPFConfig;

public interface ISessionUserManager
{
	public String getUserName(
	);
	
	public UserPFConfig getUserPFDetails(
	);
	
	public void setUserName(
	        String userName
	);
	
	public void loadPFDetails(
	);
	
	public PFBalDepSummary getPFBalDepSummary(
	);
	
	/**
	 * Determine Whether Current User Can Still Subscribe to Any Strategy or Already has all Strategies Subscribed
	 * @return - true if User Can Still Subscribe to Strategies
	 */
	public boolean CanUserSubscibeTOStrategies(
	);
	
	/**
	 * Get Strategies User Can Subscribe TO
	 * @return - Strategies the user can subscribe to
	 */
	public List<Integer> getSubscribableStrategies(
	);
	
	/**
	 * Get User Subscribed Strategies Snapshot
	 * @return - User subscribed Strategies Details
	 * @throws Exception 
	 */
	public List<UserStrategySnaphot> getUserStgySnapshots(
	) throws Exception;
	
	/**
	 * Deactivate an Already Subscribed User Strategy for the User
	 * @param id - Primary Key of User Strategy Assignment 
	 */
	public void deactivateStrategyforUser(
	        long id
	);
}
