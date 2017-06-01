/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.belsoft;

//import com.belsoft.XMLConfig.XML;
//import com.belsoft.XMLConfig.XMLMap;
import com.belsoft.config.BuildingConfig;
import com.belsoft.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
//import org.jdom.Attribute;
//import org.jdom.Document;
//import org.jdom.Element;
//import org.jdom.JDOMException;
//import org.jdom.filter.ElementFilter;
//import org.jdom.input.SAXBuilder;

/**
 *
 * @author Alecson
 */
public class Configuration {

    static HashMap map = new HashMap();

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    static ConfigLoader buildingConfig = new ConfigLoader();

    public Configuration() {
        map.put("path to xml config", "./");
        map.put("building config", map.get("path to xml config") + "building.xml");

        map.put("traders_speed", "60");
        map.put("warriors_speed", "60");
        map.put("trade_time", "10");
        map.put("attack_time", "15");
        map.put("defence_time", "20");

        map.put("mailUser Not Found", "Пользователя с таким именем не существует");
        map.put("mailUser Is Empty", "Имя получателя не может быть пустым");
        map.put("mailUser Body Is Empty", "Сообщение в письме не должно быть пустым");
        map.put("mailUser Subject Is Empty", "Тема письма не должна быть пустой");

    }

    public static byte[] deserializeString(File file)
            throws IOException {
        int size = (int) file.length() - 3;
        byte[] bytes = new byte[size];

        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        int read = 0;

        dis.read(bytes, read, 3);
        int numRead = 0;
        while (read < bytes.length && (numRead = dis.read(bytes, read,
                bytes.length - read)) >= 0) {
            read = read + numRead;
        }
        return bytes;
    }
  
    public static void readXMLDocument(String configName) {
        log.info("Loading configuration files...");

        byte[] xml;
        try {
            xml = deserializeString(new File(getPropetry("building config")));
            String cfg = new String(xml);
            buildingConfig.LoadFromString(cfg);
            log.info("Complete.");
        } catch (IOException ex) {
            log.error("Building configuration not found. Please add.");
            System.exit(0);
        }

    }

    public static String getPropetry(String name) {
        String properties = (String) map.get(name);
        if (properties == null) {
            properties = "";
        }
        return properties;
    }
    
    public static BuildingConfig getBuildingConfig(int buildingId, int race, int level) {
        BuildingConfig bConfig = new BuildingConfig();
//        log.info(Integer.decode("0x111").intValue());
        for (int i=0;i<buildingConfig.getTagChildrenCount("mapbuilding");i++) {
            if ((Integer.decode(buildingConfig.getTagAttributeValue("id", "0", "mapbuilding.map", Integer.toString(i))).intValue() == buildingId) &&
                (Integer.valueOf(buildingConfig.getTagAttributeValue("race", "0", "mapbuilding.map", Integer.toString(i))).intValue() == race)) {

                bConfig.setName(buildingConfig.getTagAttributeValue("name", "", "mapbuilding.map", Integer.toString(i)));
                bConfig.setRace(race);
                bConfig.setBuildingId(buildingId);

                bConfig.setConfigLayer(Integer.valueOf(buildingConfig.getTagAttributeValue("layer", "0", "mapbuilding.map", Integer.toString(i),"config")).intValue());
                bConfig.setConfigGfxPack(buildingConfig.getTagAttributeValue("gfxpack", "", "mapbuilding.map", Integer.toString(i),"config"));
                bConfig.setConfigPlace(Integer.valueOf(buildingConfig.getTagAttributeValue("place", "0", "mapbuilding.map", Integer.toString(i),"config")).intValue());
                bConfig.setConfigMaxLevel(Integer.valueOf(buildingConfig.getTagAttributeValue("maxlevel", "0", "mapbuilding.map", Integer.toString(i),"config")).intValue());
                bConfig.setConfigMaxQuantity(Integer.valueOf(buildingConfig.getTagAttributeValue("maxquantity", "0", "mapbuilding.map", Integer.toString(i),"config")).intValue());
                
                bConfig.setFloorRow(Integer.valueOf(buildingConfig.getTagAttributeValue("row", "0", "mapbuilding.map", Integer.toString(i),"floor")).intValue());
                bConfig.setFloorCol(Integer.valueOf(buildingConfig.getTagAttributeValue("col", "0", "mapbuilding.map", Integer.toString(i),"floor")).intValue());
                bConfig.setFloor(buildingConfig.getTagValue("mapbuilding.map", Integer.toString(i),"floor"));

                bConfig.setDescription(buildingConfig.getTagValue("mapbuilding.map", Integer.toString(i),"description"));

                for (int j=0;j<buildingConfig.getTagChildrenCountWithName("level", "mapbuilding.map",Integer.toString(i));j++) {
                    if (Integer.valueOf(buildingConfig.getTagAttributeValue("level", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j))).intValue() == level) {

                        bConfig.setLevelGfxName(buildingConfig.getTagAttributeValue("gfxname", "", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j)));
                        bConfig.setLevelDx(Integer.valueOf(buildingConfig.getTagAttributeValue("dx", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j))).intValue());
                        bConfig.setLevelDy(Integer.valueOf(buildingConfig.getTagAttributeValue("dy", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j))).intValue());
                        bConfig.setLevelTime(Integer.valueOf(buildingConfig.getTagAttributeValue("time", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j))).intValue());

                        bConfig.setConditionGrain(Integer.valueOf(buildingConfig.getTagAttributeValue("value", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j), "condition", "0")).intValue());
                        bConfig.setConditionWood(Integer.valueOf(buildingConfig.getTagAttributeValue("value", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j), "condition", "1")).intValue());
                        bConfig.setConditionStone(Integer.valueOf(buildingConfig.getTagAttributeValue("value", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j), "condition", "2")).intValue());
                        bConfig.setConditionIron(Integer.valueOf(buildingConfig.getTagAttributeValue("value", "0", "mapbuilding.map", Integer.toString(i), "level", Integer.toString(j), "condition", "3")).intValue());
                    }
                }
            }
        }
        return bConfig;
    }

}
