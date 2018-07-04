$(document).ready(function () {
    ajaxCall();
    $("#login-form").on('submit', function (event) {
        login(event);
    });
    $("#logout-form").on('submit', function (event) {
        logout(event);
    });
    $("#signup-form").on('submit', function (event) {
        signup(event);
    });
    $("#create-game").on('submit', function (event) {
        createGame(event);
    })



})
///CALLS:
function ajaxCall() {
    $.ajax({
        url: "../api/games",
        success: function (data) {
            console.log(data);
            toggleForm(data);
            var playersDTO = getPlayersDTO(data.games);

            displayGames(data);

            addScoreToPlayer(data.games, playersDTO);
            populateScoretable(orderPlayers(playersDTO));
            toggleForm(data);
            $(".join-button").on('click', function () {
                var gameId = $(this).attr("game-id");
                console.log(gameId);
                joinGame(gameId);

            });


        }

    });
}

function login(evt) {
    evt.preventDefault();
    var form = evt.target;
    $.post("/api/login", {
            name: form["username"].value,
            pwd: form["password"].value
        })
        .done(function () {
            console.log("LOGIN OK");
            form["username"].value = "";
            form["password"].value = "";
            ajaxCall();
            $("#login-modal").modal("hide");

        })
        .fail(function () {
            console.log("LOGIN FAIL");
            form["username"].value = "";
            form["password"].value = "";
            alert("Wrong user inf");

        })
}

function logout(evt) {
    evt.preventDefault();
    $.post("/api/logout")
        .done(function () {
            console.log("LOGOUT OK");
            ajaxCall();
        })
        .fail(function () {
            console.log("LOGOUT OK");

        });
}

function signup(evt) {
    evt.preventDefault();
    var form = evt.target;
    $.post("/api/players", {
            username: form["username"].value,
            password: form["password"].value
        })
        .done(function () {
            console.log("SIGNUP OK");
            login(evt);
            form["username"].value = "";
            form["password"].value = "";
            $("#signup-modal").modal("toggle");

        })
        .fail(function () {
            console.log("SIGNUP FAIL");
            form["username"].value = "";
            form["password"].value = "";
            alert("Name in use");

        })
}

function createGame(evt) {
    evt.preventDefault();
    var form = evt.target;
    $.post("/api/games")
        .done(function (data) {
            console.log("CREATE_GAME OK");
            console.log(data);
            ajaxCall();
            window.location.replace("/web/game.html?gp=" + data.gpid)
        })
        .fail(function (data) {
            console.log("CREATE_GAME FAIL");
            console.log(data);

        })
}

function joinGame(gameId) {
    $.post("/api/game/" + gameId + "/players")
        .done(function (data) {
            console.log("JOIN_GAME OK");
            ajaxCall();
            window.location.replace("/web/game.html?gp=" + data.gpid);

        })
        .fail(function () {
            console.log("JOIN_GAME FAIL");
            console.log(data);

        })

}
// /////
function toggleForm(data) {
    if (data.player !== "guest") {
        $("#notlogged-buttons").hide();

        $(".logged-buttons").show();

        $("#current-player").text("Welcome on board, captain " +
            data.player.username + "!");
    } else {
        $("#notlogged-buttons").show();

        $(".logged-buttons").hide();


        $("#current-player").text("");
    }
}

function displayGames(data) {
    $("#games-list").empty();
    for (var i = 0; i < data.games.length; i++) {
        var finished = true;
        if (data.games[i].scores.length === 0) {
            finished = false;
        }
        var row = $("<div></div>").addClass("game-info");
        var date = new Date(data.games[i].created);
        var li = $("<div></div>").text(date.toLocaleString()).addClass("game-date");
        var button = $("<button></button>").addClass("btn btn-primary game-btn");

        var players = [];

        for (var j = 0; j < data.games[i].gamePlayers.length; j++) {
            players[j] = data.games[i].gamePlayers[j].player.username;
        }

        if (data.player === "guest" || players.indexOf(data.player.username) < 0) {

            if (players.length <= 1) {

                button.text("JOIN").attr("game-id", data.games[i].id).addClass("join-button");
                if (data.player.username === players[0] || data.player === "guest") {
                    button.prop("disabled", true);
                }
                players[1] = "WAITING";

            } else {
                button.text("FULL").prop("disabled", true);
            }
        } else {
            var id = getGPID(data.games[i].gamePlayers, data.player);
            if (players.length <= 1) {
                players[1] = "WAITING";
            }
            button.attr("onclick", "window.location='/web/game.html?gp=" + id + "';")
            if (finished) {
                button.text("DONE");
            } else {
                button.text("PLAY");
            }

        }
        var player1 = $("<p></p>").text(players[0]).addClass("player1");
        var player2 = $("<p></p>").text(players[1]);
        var vs = $("<p></p>").text("VS").addClass("vs");
        var div = $("<div></div>").addClass("game-players");

        if (players[1] === "WAITING") {
            player2.addClass("waiting-player");
        } else {
            player2.addClass("player2");
        }
        var result = whoWon(data.games[i].scores);
        if (data.games[i].scores.length > 0) {
            i

            switch (true) {
                case result === 0:
                    $(player1).addClass("tie");
                    $(player2).addClass("tie");
                    break;
                case result === -1:
                    break;

                case result > 0:
                    if (getWinnerPlayer(data.games[i].gamePlayers, result) === player1.text()) {
                        $(player1).addClass("winner");
                        $(player2).addClass("looser");
                    } else if (getWinnerPlayer(data.games[i].gamePlayers, result) === player2.text()) {
                        $(player1).addClass("looser");
                        $(player2).addClass("winner");
                    }
                    break;
                default:
                    break;
            }
        }





        div.append(player2);
        div.prepend(vs);
        div.prepend(player1);

        $(row).prepend(button);
        $(row).prepend(div);
        $(row).prepend(li);

        $("#games-list").append(row);
    }

}

function whoWon(scores) {
    if (scores.length !== 0) {
        if (scores[0].score === scores[1].score) {
            return 0;
        }
        var orderedscores = scores.sort(function (a, b) {
            return b.score - a.score;
        });

        return orderedscores[0].playerId;
    } else {
        return -1;
    }
}

function getWinnerPlayer(gamePlayers, id) {
    var playerName;
    for (var i = 0; i < gamePlayers.length; i++) {
        if (gamePlayers[i].player.id === id) {
            playerName = gamePlayers[i].player.username;
        }
    }
    return playerName;
}

function getGPID(gamePlayers, player) {
    var id;
    for (var i = 0; i < gamePlayers.length; i++) {
        if (gamePlayers[i].player.id === player.id) {
            id = gamePlayers[i].id;
        }
    }
    return id;
}

function populateScoretable(players) {
    $("#scoretable").empty();
    for (var i = 0; i < players.length; i++) {
        var tr = $("<tr></tr>");
        for (var j = 0; j < 5; j++) {
            var td = $("<td></td>");
            switch (j) {
                case 0:
                    td.text(players[i].username)
                    break;
                case 1:
                    td.text(players[i].wins)
                    break;
                case 2:
                    td.text(players[i].loses)
                    break;
                case 3:
                    td.text(players[i].draws)
                    break;
                case 4:
                    td.text(players[i].total)
                    break;
                default:
                    break;
            }
            tr.append(td);
        }
        $("#scoretable").append(tr);
    }
}

function addScoreToPlayer(data, players) {

    for (var i = 0; i < data.length; i++) {
        for (var j = 0; j < data[i].scores.length; j++) {

            var currentPlayer = $.grep(players, function (obj) {
                return obj.id === parseInt(data[i].scores[j].playerId)
            })[0];

            currentPlayer.total += data[i].scores[j].score;

            switch (data[i].scores[j].score) {
                case 0:
                    currentPlayer.loses += 1;
                    break;
                case 0.5:
                    currentPlayer.draws += 1;
                    break;
                case 1:
                    currentPlayer.wins += 1;
                    break;
                default:
                    break;
            }

        }
    }

}

function getPlayersDTO(data) {
    var players = [];
    for (var j = 0; j < data.length; j++) {

        for (var i = 0; i < data[j].gamePlayers.length; i++) {
            if (!containsPlayer(data[j].gamePlayers[i].player, players)) {
                players.push(data[j].gamePlayers[i].player);
            }
        }

    }
    initPlayerObject(players);
    return players;
}

function orderPlayers(players) {
    return players.sort(function (a, b) {
        return b.total - a.total;
    });
}

function containsPlayer(obj, list) {

    for (var i = 0; i < list.length; i++) {
        if (list[i].id === obj.id) {
            return true;
        }
    }

    return false;
}

function initPlayerObject(players) {
    for (var i = 0; i < players.length; i++) {
        players[i].wins = 0;
        players[i].draws = 0;
        players[i].loses = 0;
        players[i].total = 0;
    }
}
