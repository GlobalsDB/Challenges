/**
 * Globals for the Environment
 * - an app for visualizing the results of GGSMR - the Globals Big Geospatial Data MapReducer
 */

var
connect = require('connect'),
fs = require('fs');

var PORT = process.env.PORT || 80;

var PUBLIC_DIR = __dirname + '/public/';

var MIN_YEAR = 1957;
var MAX_YEAR = 2011;

var routeParamsMap = {};

var RAW_DATA_PATH = '/raw-data';

var dataForMapSquares = loadDataForMapSquares();
var graphTemplate = loadGraphTemplate();

connect(
    connect.logger(),
    connect.static(PUBLIC_DIR),
    connect.router(function(app) {
        app.get('/', showForm);
        addMapSquareRoutes(app, 30);
        addMapSquareRoutes(app, 90);
    })
).listen(PORT);

function showForm(req, res, next) {
    res.statusCode = 302;
    res.setHeader('Location', 'index.html');
    res.end();
}

function addMapSquareRoutes(app, degreesSpan) {
    addMapSquareRoutesPolarity(app, degreesSpan, 'N', 'W');
    addMapSquareRoutesPolarity(app, degreesSpan, 'S', 'W');
    addMapSquareRoutesPolarity(app, degreesSpan, 'N', 'E');
    addMapSquareRoutesPolarity(app, degreesSpan, 'S', 'E');
}

function addMapSquareRoutesPolarity(app, degreesSpan, latPolarity, lonPolarity) {
    var MAX_LATITUDE = 90;
    var MAX_LONGITUDE = 180;

    for (var lat = 0; lat < MAX_LATITUDE; lat += degreesSpan) {
        var latUp = latPolarity == 'N' ? lat + degreesSpan : -1*lat;
        var latDown = latPolarity == 'N' ? lat : -1*lat - degreesSpan;

        for (var lon = 0; lon < MAX_LONGITUDE; lon += degreesSpan) {
            var path = '/' + degreesSpan + 'deg_' + latPolarity + lat + lonPolarity + lon;
            app.get(path, showRegionGraph);
            app.get(path + RAW_DATA_PATH, showRegionData);

            var lonLeft = lonPolarity == 'E' ? lon : -1*lon - degreesSpan;
            var lonRight = lonPolarity == 'E' ? lon + degreesSpan : -1*lon;

            routeParamsMap[path] = {'degreesSpan': degreesSpan, 'latUp': latUp, 'latDown': latDown, 'lonLeft': lonLeft, 'lonRight': lonRight};
        }
    }

//    for (var path in routeParamsMap) {
//        console.log(path + ' ' + JSON.stringify(routeParamsMap[path]));
//    }
}

function showRegionGraph(req, res, next) {
    var NO_VALUE = 0;
    var params = routeParamsMap[req.url];
    var table = [['Average'], ['Maximum'], ['Minimum']];

    var regionDesc = buildRegionDesc(params.latUp, params.latDown, params.lonLeft, params.lonRight, year);

    var years = [];
    for (var year = MIN_YEAR; year <= MAX_YEAR; year++) {
        years.push(year);

        var key = buildKey(params.latUp, params.latDown, params.lonLeft, params.lonRight, year);
        var data = dataForMapSquares[params.degreesSpan][key];
        if (data) {
            var i = 0;
            table[i++].push(Math.round(data.average));
            table[i++].push(Math.round(data.max));
            table[i++].push(Math.round(data.min));
        }
        else {
            var i = 0;
            table[i++].push(NO_VALUE);
            table[i++].push(NO_VALUE);
            table[i++].push(NO_VALUE);
        }
    }
    years = '[' + years.toString() + ']';

    var raw_data = '[';
    for (var i = 0; i < table.length; i++) {
        if (i > 0)
            raw_data += ',';
        raw_data += '[';
        var row = table[i];
        for (var j = 0; j < row.length; j++) {
            var col = row[j];
            if (j == 0)
                col = "'" + col + "'";
            else
                raw_data += ',';
            raw_data += col
        }
        raw_data += ']';
    }
    raw_data += ']';
//    console.log(raw_data);

    res.writeHead(200, {'Content-Type': 'text/html'});

    var html = graphTemplate;
    html = html.replace('${REGION_DESC}', regionDesc);
    html = html.replace('${REGION_DESC}', regionDesc);
    html = html.replace('${YEARS}', years);
    html = html.replace('${RAW_DATA}', raw_data);
    html = html.replace('${RAW_DATA_URL}', req.url + RAW_DATA_PATH);

    res.end(html);
}

function showRegionData(req, res, next) {
    var params = routeParamsMap[req.url.replace(RAW_DATA_PATH, '')];

    var out = JSON.stringify(params) + '<br>';
    for (var year = MIN_YEAR; year <= MAX_YEAR; year++) {
        out += year + ' ';
        var key = buildKey(params.latUp, params.latDown, params.lonLeft, params.lonRight, year);
        var data = dataForMapSquares[params.degreesSpan][key];
        if (data)
            out += JSON.stringify(data);
        else
            out += 'No data was collected in this region in ' + year;
        out += '<br>';
    }

    res.writeHead(200, {'Content-Type': 'text/html'});
    res.end(out);
}

function loadDataForMapSquares() {
    var data = {};
    data[30] = loadData('OceanCO2RegionYearlyStats-30.csv');
    data[90] = loadData('OceanCO2RegionYearlyStats-90.csv');
//    for (degreesSpan in data) {
//        for (key in data[degreesSpan]) {
//            console.log(degreesSpan + ' ' + key + ' ' + JSON.stringify(data[degreesSpan][key]));
//        }
//    }
    return data;
}

function loadData(file) {
    var data = {};
    fs.readFileSync(PUBLIC_DIR + file).toString().split('\n').
        forEach(function (line) {
            var tokens = line.split(',');
            if (tokens.length >= 9) {
                var i = 0;
                var latUp = formatToken(tokens[i++]);
                var latDown = formatToken(tokens[i++]);
                var lonLeft = formatToken(tokens[i++]);
                var lonRight = formatToken(tokens[i++]);
                var year = formatToken(tokens[i++]);
                var samples = formatToken(tokens[i++]);
                var min = formatToken(tokens[i++]);
                var max = formatToken(tokens[i++]);
                var average = formatToken(tokens[i++]);

                var key = buildKey(latUp, latDown, lonLeft, lonRight, year);
                data[key] = {'samples': samples, 'min': min, 'max': max, 'average': average};
//                console.log(key + ' ' + JSON.stringify(data[key]));
            }
    });
    return data;
}

function buildKey(latUp, latDown, lonLeft, lonRight, year) {
    return latUp + ',' + latDown + ',' + lonLeft + ',' + lonRight + ',' + year;
}

function buildRegionDesc(latUp, latDown, lonLeft, lonRight) {
    var latPolarity = latUp > 0 || latDown > 0 ? 'N' : 'S';
    var lonPolarity = lonLeft > 0 || lonRight > 0 ? 'E' : 'W';
    var latMin = Math.min(Math.abs(latUp), Math.abs(latDown));
    var latMax = Math.max(Math.abs(latUp), Math.abs(latDown));
    var lonMin = Math.min(Math.abs(lonLeft), Math.abs(lonRight));
    var lonMax = Math.max(Math.abs(lonLeft), Math.abs(lonRight));
    return latPolarity + latMin + '&deg;&#8211;' + latMax + '&deg;, ' + lonPolarity + lonMin + '&deg;&#8211;' + lonMax + '&deg;';
}
function formatToken(token) {
	return token.replace(/[\r\s]/g, '');
}

function loadGraphTemplate() {
    return fs.readFileSync(__dirname + '/graphTemplate.html').toString();
}
