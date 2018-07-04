var GRID_SIZE_Y = 10;
var GRID_SIZE_X = 10;
var SHIPS_COLOR = "purple";
var SALVOES_COLOR = "red";
var ENEMY_SALVOES_COLOR = "gray";

$(document).ready(function () {

    ajaxCall();
    $("#logout-form").on('submit', function (event) {
        logout(event);
    });
})

function ajaxCall() {
    $.ajax({
        url: "../api/game_view/" + paramObj(window.location.search).gp,
        success: function (data) {
            console.log(data);
            var gamePlayerId = parseInt(paramObj(window.location.search).gp);
            var gamePlayer = getCurrentGamePlayer(data.gamePlayers, gamePlayerId);
            displayGrid("#ships-locations");
            displayGrid("#salvoes-locations");
            populateAxis("#ships-locations");
            populateAxis("#salvoes-locations");



            displayShips(data.ships, SHIPS_COLOR);

            displayMySalvoes(data, gamePlayerId, SALVOES_COLOR);

            displayOponentSalvoes(data, gamePlayerId, ENEMY_SALVOES_COLOR);

            displayPlayers(data.gamePlayers, gamePlayerId);
        }
    });
}

function logout(evt) {
    evt.preventDefault();
    $.post("/api/logout")
        .done(function () {
            console.log("LOGOUT OK");
            window.location.replace("/web/games.html");
        })
        .fail(function () {
            console.log("LOGOUT OK");

        });
}

function postShips() {

    $.post({
            url: "/api/games/players/" + paramObj(window.location.search).gp + "/ships",
            data: JSON.stringify([{
                "type": "destroyer",
                "locations": ["A1", "B1", "C1"]
            }, {
                "type": "patrol boat",
                "locations": ["H5", "H6"]
            }]),
            contentType: 'application/json'

        })
        .done(function () {
            console.log("ok");
            ajaxCall();
        })
        .fail(function () {
            console.log("no");
        })
}

function displayGrid(destination) {
    for (var i = 0; i < GRID_SIZE_Y + 1; i++) {
        var tr = $("<tr></tr>");
        for (var j = 0; j < GRID_SIZE_X + 1; j++) {
            var td = $("<td></td>").addClass("cell").attr("status", "empty");
            tr.append(td);
        }
        $(destination).append(tr);
        $("tr td:first-child").addClass("cell-alpha").attr("status", "axis");
        $(destination + " tr:first-child").addClass("row-number");
        $(".row-number > td").addClass("cell-number").attr("status", "axis");
    }
}

function populateAxis(destination) {
    for (var i = 1; i < $(destination + " > .row-number > .cell-number").length; i++) {
        $(destination + " > .row-number > .cell-number:eq(" + i + ")").text(i);
    }
    for (var i = 1; i < $(destination + " > tr > .cell-alpha").length; i++) {
        var chr = String.fromCharCode(64 + i);
        $(destination + " > tr > .cell-alpha:eq(" + i + ")").text(chr);
    }
}

function paramObj(search) {
    var obj = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    search.replace(reg, function (match, param, val) {
        obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });

    return obj;
}

function displayShips(ships, color) {
    for (var i = 0; i < ships.length; i++) {
        paintShips(translateLocation(ships[i].locations), color);
    }
}

function getCurrentPlayerId(gamePlayers, id) {
    var playerId = 0;
    for (var i = 0; i < gamePlayers.length; i++) {
        if (gamePlayers[i].id === id) {
            playerId = gamePlayers[i].player.id;
        }
    }
    return playerId;
}

function getOponentPlayerId(gamePlayers, id) {
    var playerId = 0;
    for (var i = 0; i < gamePlayers.length; i++) {
        if (gamePlayers[i].id !== id) {
            playerId = gamePlayers[i].player.id;
        }
    }
    return playerId;
}

function getCurrentGamePlayer(gamePlayers, id) {
    var gamePlayer;
    for (var i = 0; i < gamePlayers.length; i++) {
        if (gamePlayers[i].id === id) {
            gamePlayer = gamePlayers[i];
        }
    }
    return gamePlayer;
}

function displayOponentSalvoes(data, id, color) {
    var playerId = getOponentPlayerId(data.gamePlayers, id);
    if (playerId > 0) {
        for (var i = 0; i < Object.keys(data.salvoes[playerId]).length; i++) {
            var index = (i + 1).toString();
            paintOponentSalvoes(translateLocation(data.salvoes[playerId][index]), index, color);

        }
    }
}

function displayMySalvoes(data, id, color) {
    var playerId = getCurrentPlayerId(data.gamePlayers, id);
    if (playerId > 0) {
        for (var i = 0; i < Object.keys(data.salvoes[playerId]).length; i++) {
            var turn = (i + 1).toString();
            paintMySalvoes(translateLocation(data.salvoes[playerId][turn]), turn, color);

        }
    }
}

function translateLocation(locations) {
    var translatedLocation = [];
    var x;
    var y;
    for (var i = 0; i < locations.length; i++) {
        var location = [];
        x = locations[i].charCodeAt(0) - 64;
        y = parseInt(locations[i].slice(1));
        location[0] = x;
        location[1] = y;
        translatedLocation.push(location);

    }
    return translatedLocation;
}

function paintShips(positions, color) {
    for (var i = 0; i < positions.length; i++) {
        $("#ships-locations > tr:eq(" + positions[i][0] + ") > .cell:eq(" + positions[i][1] + ")").css("background-color", color).addClass("ship-loc").attr("status", "ok-ship");
    }

}

function paintMySalvoes(positions, turn, color) {
    for (var i = 0; i < positions.length; i++) {
        $("#salvoes-locations > tr:eq(" + positions[i][0] + ") > .cell:eq(" + positions[i][1] + ")").css("background-color", color).text(turn).addClass("shot-loc").attr("status", "shot");
    }
}

function paintOponentSalvoes(positions, turn, color) {
    for (var i = 0; i < positions.length; i++) {
        var cell = $("#ships-locations > tr:eq(" + positions[i][0] + ") > .cell:eq(" + positions[i][1] + ")");
        if (cell.attr("status") === "ok-ship") {
            cell.css("background-color", "green").text(turn).attr("status", "hit");
        } else {
            cell.css("background-color", color).text(turn).addClass("shot-loc").attr("status", "miss");
        }

    }
}

function displayPlayers(players, id) {
    var observer;
    var opponent;
    for (var i = 0; i < players.length; i++) {
        if (players.length === 1) {
            observer = players[i].player.username;
            opponent = "WAITING";
        } else {
            if (players[i].id === id) {
                observer = players[i].player.username;
            } else {

                opponent = players[i].player.username;

            }
        }
    }
    $("#players").text(observer + "(you) vs. " + opponent);
}
