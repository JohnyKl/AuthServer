/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.belsoft.managers;

/**
 *
 * @author Alecson
 */
public class EventManager {

    /*
    static public ActionList getActionList(int city_id) {
        MySQLDataManager dataManager = null;
        ActionList actionList = null;
        try
        {
            dataManager = new MySQLDataManager();
            actionList = dataManager.getActionList(city_id);
            dataManager.closeConnection();

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
        return actionList;
    }

    static public void SendAttack(int city_id, int city_id2) {
        MySQLDataManager dataManager = null;
        try
        {
            dataManager = new MySQLDataManager();
            WorldObject city_source = dataManager.getCityInfo(city_id);
            WorldObject city_target = dataManager.getCityInfo(city_id2);

            int time = (int)(Math.round(Math.sqrt(3600*(
                                        Math.pow(city_source.x - city_target.x, 2) +
                                        Math.pow(city_source.y - city_target.y, 2)))));
            
            dataManager.addAction(city_source.user_id, city_target.user_id, city_id, city_id2, 0, 0, time, ActionType.EVENTMYATTACK, 0, true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
    }

    static public void HirePopulation(int city_id, int cnt) {
        MySQLDataManager dataManager = null;
        try
        {
            dataManager = new MySQLDataManager();
            WorldObject city_source = dataManager.getCityInfo(city_id);

            int time = cnt*60;
            
            dataManager.addAction(city_source.user_id, 0, city_id, 0, 0, 0, time, ActionType.EVENTUNITCREATE, cnt, false);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
    }

    static public void ReturnAttack(int city_id, int city_id2) {
        MySQLDataManager dataManager = null;
        try
        {
            dataManager = new MySQLDataManager();
            WorldObject city_source = dataManager.getCityInfo(city_id);
            WorldObject city_target = dataManager.getCityInfo(city_id2);

            int time = (int)(Math.round(Math.sqrt(3600*(
                                        Math.pow(city_source.x - city_target.x, 2) +
                                        Math.pow(city_source.y - city_target.y, 2)))));
            
            dataManager.addAction(city_source.user_id, 0, city_id, 0, 0, 0, time, ActionType.EVENTMYATTACKRETURN, 0, true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
    }
    
    static public void SendTraders(int city_id, int city_id2) {
        MySQLDataManager dataManager = null;
        try
        {
            dataManager = new MySQLDataManager();
            WorldObject city_source = dataManager.getCityInfo(city_id);
            WorldObject city_target = dataManager.getCityInfo(city_id2);

            int time = (int)(Math.round(Math.sqrt(3600*(
                                        Math.pow(city_source.x - city_target.x, 2) +
                                        Math.pow(city_source.y - city_target.y, 2)))));
            
            dataManager.addAction(city_source.user_id, city_target.user_id, city_id, city_id2, 0, 0, time, ActionType.EVENTMYTRADE, 0, true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
    }

    static public void ReturnTraders(int city_id, int city_id2) {
        MySQLDataManager dataManager = null;
        try
        {
            dataManager = new MySQLDataManager();
            WorldObject city_source = dataManager.getCityInfo(city_id);
            WorldObject city_target = dataManager.getCityInfo(city_id2);

            int time = (int)(Math.round(Math.sqrt(3600*(
                                        Math.pow(city_source.x - city_target.x, 2) +
                                        Math.pow(city_source.y - city_target.y, 2)))));
            
            dataManager.addAction(city_source.user_id, 0, city_id, 0, 0, 0, time, ActionType.EVENTMYTRADERETURN, 0, true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
    }

    static public void SendDefense(int city_id, int city_id2) {
        MySQLDataManager dataManager = null;
        try
        {
            dataManager = new MySQLDataManager();
            WorldObject city_source = dataManager.getCityInfo(city_id);
            WorldObject city_target = dataManager.getCityInfo(city_id2);

            int time = (int)(Math.round(Math.sqrt(3600*(
                                        Math.pow(city_source.x - city_target.x, 2) +
                                        Math.pow(city_source.y - city_target.y, 2)))));
            
            dataManager.addAction(city_source.user_id, city_target.user_id, city_id, city_id2, 0, 0, time, ActionType.EVENTMYDEFENSE, 0, true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
    }

    static public void ReturnDefense(int city_id, int city_id2) {
        MySQLDataManager dataManager = null;
        try
        {
            dataManager = new MySQLDataManager();
            WorldObject city_source = dataManager.getCityInfo(city_id);
            WorldObject city_target = dataManager.getCityInfo(city_id2);

            int time = (int)(Math.round(Math.sqrt(3600*(
                                        Math.pow(city_source.x - city_target.x, 2) +
                                        Math.pow(city_source.y - city_target.y, 2)))));
            
            dataManager.addAction(city_source.user_id, 0, city_id, 0, 0, 0, time, ActionType.EVENTMYDEFENSERETURN, 0, true);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            dataManager.closeConnection();
        }
    }
     * 
     */
}
