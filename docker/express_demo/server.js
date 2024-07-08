var express = require('express');
var app = express();


app.get('/', function (req, res) {
  res.send('Hello World! How are you doing? Right! ok!');
});


app.get('/hello/:name', function (req, res) {
  res.send('Hello ' + req.params.name + ' :-)');
});


app.listen(5000, function () {
  console.log('Listening on port 5000!');
});