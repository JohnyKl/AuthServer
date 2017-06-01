package com.dreamlex.entity;

import org.apache.commons.lang3.RandomUtils;

import javax.persistence.*;

@Entity
@Table(name = "offsets")
public class Offset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "game_id", unique = false, nullable = false)
    private Integer gameID;

    @Column(name = "game_version", unique = true, nullable = false)
    private Integer gameVersion;

    @Column(name = "data", unique = false, nullable = false)
    private byte[] data;

    public Offset() { }

    public Offset(Integer gameID, Integer gameVersion, byte[] data) {
        this.gameID = gameID;
        this.gameVersion = gameVersion;
        this.data = data != null ? data : generateRandomData();
    }

    private byte[] generateRandomData() {
        return RandomUtils.nextBytes(20);
    }

    public int getID () {return id;}
    public int getGameID () { return gameID; }
    public int getGameVersion () { return gameVersion; }
    public byte[] getData () { return data; }

    public void setGameID (int gameID) { this.gameID = gameID; }
    public void setGameVersion (int gameVersion) { this.gameVersion = gameVersion; }
    public void setData (byte[] data) { this.data = data; }
}
