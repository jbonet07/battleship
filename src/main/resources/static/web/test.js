$(document).ready(function () {
    //createSampleGrid();

    var container1 = document.querySelector('#left');
    var container2 = document.querySelector('#right');
    var drake = dragula({
        copy: true
    });
    drake.containers.push(container1);
    drake.containers.push(container2);

})


function createSampleGrid() {
    for (var i = 0; i < 10; i++) {
        var tr = $("<tr></tr>");
        for (var j = 0; j < 10; j++) {
            var td = $("<td></td>");
            tr.append(td);
        }
        $("#test-grid").append(tr);
    }
}
