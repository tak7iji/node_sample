
/**
 * Module dependencies.
 */

var express = require('express')
  , io = require('socket.io')
  , routes = require('./routes')
  , http = require('http')
  , fs = require('fs')
  , path = require('path');

var app = express();

app.configure(function(){
  app.set('port', process.env.PORT || 3000);
  app.set('views', __dirname + '/views');
  app.set('view engine', 'ejs');
  app.use(express.favicon());
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(path.join(__dirname, 'public')));
});

app.configure('development', function(){
  app.use(express.errorHandler());
});

app.get('/', routes.index);

var server = http.createServer(app).listen(app.get('port'), function(){
  console.log("Express server listening on port " + app.get('port'));
});

var logList = new Array();
var socket = io.listen(server, {'log level' : 0, 'heartbeat interval' : 600, 'transports' : ['websocket']});
socket.on('connection', function(socket) {
  socket.on('message', function(msg) {
    var date = +new Date();
    var log = msg+', '+date+'\n';
    logList.push(log);
  });
  socket.on('get', function(msg) {
    fs.open('./log.csv', 'w', 777, function(err, fd) {
      for(i=0; i < logList.length; i++){
        var buf = new Buffer(logList[i]);
        fs.writeSync(fd, buf, 0, buf.length, null);
      }
      fs.close(fd);
    });
  });
});
 
