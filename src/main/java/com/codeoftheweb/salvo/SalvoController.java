package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;


import static java.util.stream.Collectors.toList;

@RequestMapping("/api")
@RestController
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping(path = "/players", method=RequestMethod.POST)
    public ResponseEntity< Map<String, Object>> createPlayer(String username, String password){
        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(statusMap("error", "Empty field"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(username);
        if (player != null) {
            return new ResponseEntity<>(statusMap("error", "Name already in use"), HttpStatus.CONFLICT);
        }

        Player p1 = playerRepository.save(new Player(username, password));
        return new ResponseEntity<>(statusMap("succes", p1.getUserName()), HttpStatus.CREATED);
    }
    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShip(Authentication authentication, @PathVariable Long gamePlayerId, @RequestBody List<Ship> ships){
        if(authentication == null) {
            return new ResponseEntity<>(statusMap("error", "Not logged"), HttpStatus.UNAUTHORIZED);
        }
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        if(gamePlayer == null){
            return new ResponseEntity<>(statusMap("error", "No such gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        if(gamePlayer.getPlayer().getId() != player.getId()){
            return new ResponseEntity<>(statusMap("error", "Not you gamePlayer"), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer.getShips().size() > 0){
            return new ResponseEntity<>(statusMap("error", "Ships already placed"), HttpStatus.FORBIDDEN);
        }
        for(Ship ship : ships){
            gamePlayer.addShip(ship);
            ship.setGamePlayer(gamePlayer);
            shipRepository.save(ship);
        }
        return new ResponseEntity<>(statusMap("succes", gamePlayer.getId()), HttpStatus.CREATED);
    }
    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long gameId){
        if(authentication == null) {
            return new ResponseEntity<>(statusMap("error", "Not logged"), HttpStatus.UNAUTHORIZED);
        }

        Game game = gameRepository.findOne(gameId);
        if(game == null){return new ResponseEntity<>(statusMap("error", "No such game"), HttpStatus.FORBIDDEN);}


        if(game.getPlayers().size() >= Game.MAX_PLAYERS){
            return new ResponseEntity<>(statusMap("error", "Full game"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer =  gamePlayerRepository.save(new GamePlayer(game, player));
        return new ResponseEntity<>(statusMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getAll(Authentication authentication) {
        Map<String, Object> all = new LinkedHashMap<>();
        Object player;
        if(authentication==null){
            player ="guest";
        } else {
            player = currentPlayer(authentication.getName());
        }
        all.put("player", player);
        all.put("games", gameRepository.findAll().stream()
                .map(b -> getGameDTO(b))
                .collect(toList()));
        return all;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
        if(authentication==null){
            return new ResponseEntity<>(statusMap("error", "No user"), HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        Game game = gameRepository.save(new Game());
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, player));
        return new ResponseEntity<>(statusMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> permitAcces(Authentication authentication, @PathVariable Long gamePlayerId) {
        Player player = playerRepository.findByUserName(authentication.getName());
        for(GamePlayer gamePlayer: player.getGamePlayers()){
            if(gamePlayer.getId() == gamePlayerId){
                return new ResponseEntity<>(getGPlayerDTO(gamePlayer), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(statusMap("error", "Trying to cheat?"), HttpStatus.UNAUTHORIZED);
    }
    private Map<String, Object> statusMap(String key, Object value){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }
    public Map<String, Object> currentPlayer(String userName) {
        Map<String, Object> playerDTO = new LinkedHashMap<>();
        Player player = playerRepository.findByUserName(userName);
        playerDTO.put("id", player.getId());
        playerDTO.put("username", player.getUserName());
        return playerDTO;

    }


    public Map<String, Object> getGameDTO(Game game) {
        Map<String, Object> games_created = new LinkedHashMap<String, Object>();
        games_created.put("id", game.getId());
        games_created.put("created", game.getCreationDate());
        games_created.put("gamePlayers", getGameGPlayerListDTO(game));
        games_created.put("scores", getScoresDTO(game));

        return games_created;
    }

    public Map<String, Object> getGPlayerDTO(GamePlayer gamePlayer){
        Map<String, Object> gameplayers_info = new LinkedHashMap<String, Object>();
        gameplayers_info.put("id", gamePlayer.getGame().getId());
        gameplayers_info.put("created", gamePlayer.getGame().getCreationDate());
        gameplayers_info.put("gamePlayers", getGameGPlayerListDTO(gamePlayer.getGame()));
        gameplayers_info.put("ships", getShipsDTO(gamePlayer));
        gameplayers_info.put("salvoes", getPlayerSalvoesDTO(gamePlayer.getGame()));
        return gameplayers_info;
    }

    public List<Object> getGameGPlayerListDTO(Game game) {

        return game.getGamePlayers().stream()
        .map(gamePlayer -> getGPlayerMapDTO(gamePlayer))
        .collect(toList());
    }

    public Map<String, Object> getGPlayerMapDTO(GamePlayer gamePlayer) {
        Map<String, Object> game_players = new LinkedHashMap<>();
        game_players.put("id", gamePlayer.getId());
        game_players.put("player", getPlayerDTO(gamePlayer.getPlayer()));
        return  game_players;
    }
    public  Map<String, Object> getPlayerDTO (Player player) {
        Map<String, Object> players = new LinkedHashMap<>();
        players.put("id", player.getId());
        players.put("username", player.getUserName());
        return  players;
    }
    public List<Object> getShipsDTO (GamePlayer gamePlayer){

        return gamePlayer.getShips().stream()
                .map(ship -> getShipsMapDTO(ship))
                .collect(toList());
    }
    public Map<String, Object> getShipsMapDTO (Ship ship) {
        Map<String, Object> ships = new LinkedHashMap<>();
        ships.put("type", ship.getType());
        ships.put("locations", ship.getLocations());
        return ships;
    }
    public Map<String, Object> getPlayerSalvoesDTO(Game game) {
        Map<String, Object> playerSalvoes = new LinkedHashMap<>();
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            playerSalvoes.put("" + gamePlayer.getPlayer().getId(), getSalvoesDTO(gamePlayer));
        }
        return playerSalvoes;
    }
    public Map<String, Object> getSalvoesDTO(GamePlayer gamePlayer){
        Map<String, Object> salvoes = new LinkedHashMap<>();
        for(Salvo salvo: gamePlayer.getSalvoes()){
            salvoes.put("" + salvo.getTurn(), salvo.getLocations());
        }
        return salvoes;
    }
    public List<Object> getScoresDTO(Game game){
        return game.getScores().stream()
                .map(score -> getScoresMapDTO(score))
                .collect(toList());
    }
    public Map<String, Object> getScoresMapDTO(Score score){
        Map<String, Object> scores = new LinkedHashMap<>();
        scores.put("playerId", score.getPlayer().getId());
        scores.put("score", score.getScore());

        return scores;
    }
}
