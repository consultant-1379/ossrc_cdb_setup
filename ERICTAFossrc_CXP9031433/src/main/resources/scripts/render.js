var system = require('system');
var page = require('webpage').create();





page.open('Chart.html', function (status) {

console.log(""+status);

page.onConsoleMessage = function (msg) {
console.log("--------"+msg);
      
  if (msg === 'Google.chart.ready') {
    window.ischartready = true;
    page.render(system.args[1]);
        phantom.exit();
  }
}



});

