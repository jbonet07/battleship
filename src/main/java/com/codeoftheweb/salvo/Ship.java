package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String type;

    @ElementCollection
    @Column(name="location")
    private List<String> locations =  new ArrayList<>();

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {
        System.out.println(this);
    }


    public Ship(GamePlayer gamePlayer, String type, List<String> locations) {
        this.gamePlayer = gamePlayer;
        this.gamePlayer.addShip(this);
        this.type = type;
        this.locations = locations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
