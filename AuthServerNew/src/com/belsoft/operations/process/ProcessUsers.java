package com.belsoft.operations.process;

import java.util.Collection;

import com.belsoft.dao.BoundaryDAO;
import com.belsoft.dao.factory.DaoFactory;
import com.belsoft.entity.User;
import com.belsoft.exceptions.CardFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays the information and performs the user's commands
 * @author ag
 *
 */
public class ProcessUsers {
    private static final Logger log = LoggerFactory.getLogger(ProcessUsers.class);
        static public void addUser(User user)  throws CardFileException {
            DaoFactory.getInstance().getUserDAO().addElement(user);
        }
        
        static public User getUser(int userId)  throws CardFileException {
            return DaoFactory.getInstance().getUserDAO().getElementByID(userId);
        }
        
        static public User addUser(String email, String username, String password, int raceId, int gender, String avatar, int currentCityId) {
                User user = null;
		try {
                        if ((getUserByEmail(email) == null) && (getUserByName(username)) == null) {
                            //Race race = ProcessUsers.getRaceById(raceId);
                            user = new User();
                            //user = new User(email, username, password, race, currentCityId, gender, avatar);
                            addUser(user);
                        }
		} catch (CardFileException e) {
			log.error(e.getMessage());
                        return null;
		}
                return user;
	}
	
	/**
	 * Asks information about a user and then adds him
	 */
	static public User getUserByEmail(String email) {
            User user = null;
		try {
			user = DaoFactory.getInstance().getUserDAO().getUserByEmail(email);
		} catch (CardFileException e) {
			log.error(e.getMessage());
		}
                return user;
	   }
   
	static public User getUserByName(String name) {
            User user = null;
		try {
			user = DaoFactory.getInstance().getUserDAO().getUserByName(name);
		} catch (CardFileException e) {
			log.error(e.getMessage());
		}
                return user;
	   }

	
    static public void deleteUser(User user) {
        DaoFactory.getInstance().getUserDAO().deleteElement(user);
    }

    static public void deleteUser(String email) {
            User user = getUserByEmail(email);

            if (user != null) {
                deleteUser(user);
            } else {
                log.debug("User with the specified email was not found");
            }
    }

    static public void updateUser(User user) throws CardFileException {
        DaoFactory.getInstance().getUserDAO().updateElement(user);
    }

    /**
     * Asks the user's name and offers to edit his info
     */
    static public void editUser(String email, String username, String password, int raceId) {
        try {
            User user = getUserByEmail(email);

            if (user != null) {

                user.setUserName(username);
                user.setPassword(password);
               // Race race = user.getRace();
                //race.setId(raceId);
                //user.setRace(race);
                updateUser(user);

            } else {
                log.debug("User with the specified email was not found");
            }
        } catch (CardFileException e) {
            log.error(e.getMessage());
        }
    }

    static public void editUserKey(String email, String cookie) {
        try {
            User user = getUserByEmail(email);

            if (user != null) {
                
                updateUser(user);

            } else {
                log.debug("User with the specified email was not found");
            }
        } catch (CardFileException e) {
            log.error(e.getMessage());
        }
    }
    /**
     * Displays the whole list of user
     */
    static public void showAllUsers() {
        try {
            Collection<User> users = BoundaryDAO.getInstance().findAllUsers();
//			ReportTable rt = UsersTransformer.buildUsersReportTable(users);
//			ShowReport.offerManagingWithReport(rt);
        } catch (Exception e) {
            // no errors expected
            log.error(e.getMessage());
        }
    }
}
