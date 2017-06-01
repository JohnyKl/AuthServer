/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.belsoft.config;

/**
 *
 * @author Alecson
 */
public class BuildingConfig {

    private int buildingId;
    private String name;
    private int race;
    
    private int configLayer;
    private String configGfxPack;
    private int configPlace;
    private int configMaxLevel;
    private int configMaxQuantity;

    private int floorRow;
    private int floorCol;
    private String floor;
    
    private String description;
    
    private int level;
    private String levelGfxName;
    private int levelDx;
    private int levelDy;
    private int levelTime;

    private int conditionGrain;
    private int conditionWood;
    private int conditionStone;
    private int conditionIron;

    public int getConditionGrain() {
        return conditionGrain;
    }

    public int getConditionIron() {
        return conditionIron;
    }

    public int getConditionStone() {
        return conditionStone;
    }

    public int getConditionWood() {
        return conditionWood;
    }

    public String getLevelGfxName() {
        return levelGfxName;
    }

    public String getConfigGfxPack() {
        return configGfxPack;
    }

    public int getConfigLayer() {
        return configLayer;
    }

    public int getConfigMaxLevel() {
        return configMaxLevel;
    }

    public int getConfigMaxQuantity() {
        return configMaxQuantity;
    }

    public int getConfigPlace() {
        return configPlace;
    }

    public String getDescription() {
        return description;
    }

    public String getFloor() {
        return floor;
    }

    public int getFloorCol() {
        return floorCol;
    }

    public int getFloorRow() {
        return floorRow;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public int getLevel() {
        return level;
    }

    public int getLevelDx() {
        return levelDx;
    }

    public int getLevelDy() {
        return levelDy;
    }

    public int getLevelTime() {
        return levelTime;
    }

    public String getName() {
        return name;
    }

    public int getRace() {
        return race;
    }

    public void setConditionGrain(int conditionGrain) {
        this.conditionGrain = conditionGrain;
    }

    public void setConditionIron(int conditionIron) {
        this.conditionIron = conditionIron;
    }

    public void setConditionStone(int conditionStone) {
        this.conditionStone = conditionStone;
    }

    public void setConditionWood(int conditionWood) {
        this.conditionWood = conditionWood;
    }

    public void setLevelGfxName(String configGfxName) {
        this.levelGfxName = configGfxName;
    }

    public void setConfigGfxPack(String configGfxPack) {
        this.configGfxPack = configGfxPack;
    }

    public void setConfigLayer(int configLayer) {
        this.configLayer = configLayer;
    }

    public void setConfigMaxLevel(int configMaxLevel) {
        this.configMaxLevel = configMaxLevel;
    }

    public void setConfigMaxQuantity(int configMaxQuantity) {
        this.configMaxQuantity = configMaxQuantity;
    }

    public void setConfigPlace(int configPlace) {
        this.configPlace = configPlace;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setFloorCol(int floorCol) {
        this.floorCol = floorCol;
    }

    public void setFloorRow(int floorRow) {
        this.floorRow = floorRow;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setLevelDx(int levelDx) {
        this.levelDx = levelDx;
    }

    public void setLevelDy(int levelDy) {
        this.levelDy = levelDy;
    }

    public void setLevelTime(int levelTime) {
        this.levelTime = levelTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRace(int race) {
        this.race = race;
    }

}
