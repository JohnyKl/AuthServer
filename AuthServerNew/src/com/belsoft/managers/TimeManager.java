package com.belsoft.managers;

import com.belsoft.exceptions.CardFileException;
import com.belsoft.*;
import com.belsoft.entity.User;
import com.belsoft.operations.process.ProcessReports;
import com.belsoft.packet.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mina.core.session.IoSession;

public class TimeManager extends Thread {

    public TimeManager(String name) {
        super(name);
    }

    @Override
    public void run() {
        /*while (true) {
            try {
                while (true) {
                    Action action = ProcessAction.getLastAction();
                    if (action != null) {
                        System.out.println("Action No:"+action.getId()+" processing.");
                        ProcessAction.deleteAction(action);
                        BuildingsList bl = null;
                        ResearchList scl = null;
                        ActionList al = null;
                        Population p = null;
                        SquadronList sl = null;
                        User user = action.getUserOwner();
                        User targetUser;
                        Squadron squadron = action.getSquadron();
                        switch(action.getType()) {
                            case ActionType.EVENTBUILD: break;
                            case ActionType.EVENTSCIENCE: scl = ProcessScienceAction(action); break;  
                            case ActionType.EVENTUPGRADE: bl = ProcessUpdateBuildingAction(action); break;  
                            case ActionType.EVENTDOWNGRADE: bl = ProcessUpdateBuildingAction(action); break;  

                            case ActionType.EVENTMYATTACK: 
                                ProcessAction.ProcessAttack(user, action.getCityOwner(), action.getCityTarget(), squadron); 
                                break;
                            case ActionType.EVENTMYATTACKPROCESS:
                                targetUser = action.getCityTarget().getUser();
                                ProcessAction.ReturnAttack(user, action.getCityOwner(), action.getCityTarget(), squadron); 
                                ProcessBattle.Battle(user, action.getUserTarget(), action.getCityOwner(), action.getCityTarget(), squadron, null);
                                break;
                                
                            case ActionType.EVENTMYATTACKRETURN: 
                                ProcessSquadrons.deleteSquadron(user, squadron); 
                                break;
          
                            case ActionType.EVENTMYDEFENSE: 
                                ProcessAction.ProcessDefense(user, action.getCityOwner(), action.getCityTarget(), squadron); 
                                break;
                            case ActionType.EVENTMYDEFENSEPROCESS: 
                                ProcessAction.ReturnDefense(user, action.getCityOwner(), action.getCityTarget(), squadron); 
                                ProcessReports.addReportMessages(user, "Ваши войска успешно возвращаются с защиты города "+action.getCityTarget().getName(), "Круто повоевали");
                                break;
                            case ActionType.EVENTMYDEFENSERETURN: ProcessSquadrons.deleteSquadron(user, squadron); 
                                break;
          
                            case ActionType.EVENTMYTRADE: 
                                ProcessAction.ProcessTraders(user, action.getCityOwner(), action.getCityTarget(), squadron); 
                                break;
                            case ActionType.EVENTMYTRADEPROCESS: 
                                targetUser = action.getCityTarget().getUser();
                                ProcessAction.ReturnTraders(user, action.getCityOwner(), action.getCityTarget(), squadron); 
                                ProcessReports.addReportMessages(user, "Ваши торговцы принесли товары в город "+action.getCityTarget().getName()+" игрока "+action.getCityTarget().getUser().getUserName(), 
                                    "Бизнес процветает.\n" +
                                    "Было принесено:\n"+
                                    "Зерна: "+squadron.getResGrain()+"\n"+
                                    "Дерева: "+squadron.getResWood()+"\n"+
                                    "Камня: "+squadron.getResStone()+"\n"+
                                    "Железа: "+squadron.getResIron()
                                        );
                                ProcessReports.addReportMessages(targetUser, "В наш город пришли торговцы игрока "+action.getCityOwner().getUser().getUserName()+" посланные с города "+action.getCityOwner().getName(), "Товаров привалило немерянно");
                                break;
                            case ActionType.EVENTMYTRADERETURN: 
                                ProcessSquadrons.deleteSquadron(user, squadron);  
                                break;
          
                            case ActionType.EVENTPRODUCTION: 
                                ProcessProductions.addProduction(action.getCityOwner(), action.getItemId(), action.getCount());
//                                ProcessAction.ProcessProduction(user, action.getCityOwner(), null, squadron); 
                                break;

                            case ActionType.EVENTUNITCREATE: break;
                        }
                        
                        synchronized (BelsoftServer.protocolHandler.sessions)
                        {
                            Iterator<IoSession> iter = BelsoftServer.protocolHandler.sessions.iterator();
                            while (iter.hasNext())
                            {
                                IoSession s = (IoSession) iter.next();
                                if ((((User)s.getAttribute("user")).getId().intValue() == action.getUserOwner().getId().intValue()) & 
                                   (s.isConnected()) & 
                                   (((User)s.getAttribute("user")).getCurrentCityId().intValue() == action.getCityOwner().getId().intValue())) {
                                        switch(action.getType()) {
                                            case ActionType.EVENTBUILD: break;
                                            case ActionType.EVENTUPGRADE: 
                                            case ActionType.EVENTDOWNGRADE: 
                                                BelsoftServer.protocolHandler.sessionWrite(s, bl); 
                                                al = new ActionList(1, ProcessAction.getActionsByCity(action.getCityOwner()));
                                                BelsoftServer.protocolHandler.sessionWrite(s, al); 
                                                break;  
                                            case ActionType.EVENTSCIENCE: 
                                                BelsoftServer.protocolHandler.sessionWrite(s, scl); 
                                                al = new ActionList(1, ProcessAction.getActionsByCity(action.getCityOwner()));
                                                BelsoftServer.protocolHandler.sessionWrite(s, al); 
                                                break;  

                                            case ActionType.EVENTPRODUCTION: 
                                                ProductionList productionList = new ProductionList(1, action.getCityOwner().getId(), 
                                                    ProcessProductions.getProductions(action.getCityOwner()));
                                                BelsoftServer.protocolHandler.sessionWrite(s, productionList);
                                                al = new ActionList(1, ProcessAction.getActionsByCity(action.getCityOwner()));
                                                BelsoftServer.protocolHandler.sessionWrite(s, al); 

                                                int population = ProcessProductions.getProduction(action.getCityOwner(), 10+user.getRace().getId().intValue());
                                                action.getCityOwner().setPopulation(population);
                                                ProcessCities.updateCity(action.getCityOwner());

//                                                p = new Population(1, action.getCityOwner().getId(), action.getCityOwner());
//                                                BelsoftServer.protocolHandler.sessionWrite(s, p); 

                                                Collection<Production> productions = ProcessProductions.getProductions(action.getCityOwner());
                                                ItemList itemList = new ItemList(1, 
                                                                            action.getCityOwner().getId(), 
                                                                            0, 
                                                                            0, 
                                                                            productions);
                                                BelsoftServer.protocolHandler.sessionWrite(s, itemList); 
                                                break;  

                                            case ActionType.EVENTMYATTACK:
                                            case ActionType.EVENTMYATTACKPROCESS:
                                            case ActionType.EVENTMYATTACKRETURN: 
                                            case ActionType.EVENTMYDEFENSE: 
                                            case ActionType.EVENTMYDEFENSEPROCESS: 
                                            case ActionType.EVENTMYDEFENSERETURN: 
                                            case ActionType.EVENTMYTRADE: 
                                            case ActionType.EVENTMYTRADEPROCESS: 
                                            case ActionType.EVENTMYTRADERETURN: 
                                                al = new ActionList(1, ProcessAction.getActionsByCity(action.getCityOwner()));
                                                BelsoftServer.protocolHandler.sessionWrite(s, al); 
                                                sl = new SquadronList(1, ProcessSquadrons.getSquadrons(user, action.getCityOwner())); 
                                                BelsoftServer.protocolHandler.sessionWrite(s, sl); 
                                                break;
                                            case ActionType.EVENTUNITCREATE:
                                                City city = action.getCityOwner();
                                                city.setPopulation(city.getPopulation()+action.getCount());
                                                ProcessCities.updateCity(city);
                                                p = new Population(1, city.getId(), city);
                                                BelsoftServer.protocolHandler.sessionWrite(s, p); 
                                                al = new ActionList(1, ProcessAction.getActionsByCity(action.getCityOwner()));
                                                BelsoftServer.protocolHandler.sessionWrite(s, al); 
                                                break;
                                        }
                                }
                                if (action.getCityTarget() != null) {
                                    if (action.getUserTarget() != null) {
                                        if (((User)s.getAttribute("user")).getId().intValue() == action.getUserTarget().getId().intValue()) {
                                            if (s.isConnected()) {
                                                switch(action.getType()) {
                                                    case ActionType.EVENTMYTRADEPROCESS: 
                                                    case ActionType.EVENTMYDEFENSEPROCESS: 
                                                    case ActionType.EVENTMYATTACKPROCESS:
                                                        ReportStatus rs = new ReportStatus(1, ProcessReports.getReportMessageUnreadCount(user), ProcessReports.getReportMessageCount(user));
                                                        BelsoftServer.protocolHandler.sessionWrite(s, rs);
                                                }
                                           
                                                if (((User)s.getAttribute("user")).getCurrentCityId().intValue() == action.getCityTarget().getId().intValue()) {
                                                    switch(action.getType()) {
                                                        case ActionType.EVENTMYATTACK:
                                                        case ActionType.EVENTMYATTACKPROCESS:
                                                        case ActionType.EVENTMYDEFENSE: 
                                                        case ActionType.EVENTMYDEFENSEPROCESS: 
                                                        case ActionType.EVENTMYTRADE: 
                                                        case ActionType.EVENTMYTRADEPROCESS: 
                                                            al = new ActionList(1, ProcessAction.getActionsByCity(action.getCityTarget()));
                                                            BelsoftServer.protocolHandler.sessionWrite(s, al); 
                                                            sl = new SquadronList(1, ProcessSquadrons.getSquadrons(action.getUserTarget(), action.getCityTarget())); 
                                                            BelsoftServer.protocolHandler.sessionWrite(s, sl); 
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Auction auction = ProcessAuctions.getLastAuction();
                        if (auction != null) {
                            ProcessAuctions.AuctionFinish(auction);
                        } else {
                            System.gc();
                            Thread.sleep(1000L);
                        }
                    }
                }
            } catch (CardFileException ex) {
                Logger.getLogger(TimeManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException interruptedexception) {
                ;
            }
        }*/
    }

    /*static public BuildingsList ProcessUpdateBuildingAction(Action action) {
        BuildingsList retVal = null;
        try
        {
            switch (action.getType()) {
                case ActionType.EVENTBUILD: break;
                case ActionType.EVENTUPGRADE: 
                    Building building = action.getBuilding();
                    building.setLevel(building.getLevel()+1);
                    ProcessBuildings.updateBuilding(building);
                    retVal = new BuildingsList(1, 
                            building.getCity().getId(),
                            building.getLayer(),
                            ProcessBuildings.getBuildingById(building.getId()));
                    break;
                case ActionType.EVENTDOWNGRADE: 
                    building = action.getBuilding();
                    building.setLevel(building.getLevel()-1);
                    ProcessBuildings.updateBuilding(building);
                    retVal = new BuildingsList(1, 
                            building.getCity().getId(),
                            building.getLayer(),
                            ProcessBuildings.getBuildingById(building.getId()));
                    break;  
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return retVal;
    }

    static public ResearchList ProcessScienceAction(Action action) {
        ResearchList retVal = null;
        try
        {
            Science science = ProcessSciences.getScience(action.getCityOwner(), action.getItemId());
                    science.setLevel(action.getCount()+1);
                    ProcessSciences.editScience(science);
                    retVal = new ResearchList(1, action.getCityOwner().getId(), 
                            ProcessSciences.getSciences(action.getCityOwner()));
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return retVal;
    }*/
}
