
/**
 * Module dependencies.
 */

var express = require('express')
  , io = require('socket.io')
  , routes = require('./routes')
  , http = require('http')
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

var socket = io.listen(server, {'log level' : 0, 'heartbeat interval' : 300, 'transports' : ['websocket']});
socket.on('connection', function(socket) {
  socket.on('message', function(msg) {
    var buf = new Buffer(msg|0);
    buf.fill(0x7f);
    socket.send(buf);
  });
});
