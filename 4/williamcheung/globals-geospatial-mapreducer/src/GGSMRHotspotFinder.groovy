// GGSMRHotspotFinder program

// default parameters
REDUCER_OUTPUT_GLOBAL = 'reducerOutput.yearly.30x30'
TOP_AVG_RANKINGS_GLOBAL = 'topAvgRankings.mostRecent.yearly'
TOP = 10
LATEST_YEAR_WITH_DATA = 2011
MAX_YEARS_TO_GO_BACK_FROM_LATEST = 1
OUTPUT_FILE = "/top${TOP}AvgRankings.csv"

// parse command line
def cli = new CliBuilder(usage:
    "groovy GGSMRHotspotFinder -r reducerOutputGlobal -t $TOP -l $LATEST_YEAR_WITH_DATA -m $MAX_YEARS_TO_GO_BACK_FROM_LATEST -o $OUTPUT_FILE")
cli.h(longOpt: 'help', 'usage')
cli.r(longOpt: 'reducerOutputGlobal', "default: $REDUCER_OUTPUT_GLOBAL", args: 1)
cli.t(longOpt: 'top', "default: $TOP", args: 1)
cli.l(longOpt: 'latestYearWithData', "default: $LATEST_YEAR_WITH_DATA", args: 1)
cli.m(longOpt: 'maxYearsToGoBackFromLatest', "default: $MAX_YEARS_TO_GO_BACK_FROM_LATEST", args: 1)
cli.o(longOpt: 'outputFile', "default: $OUTPUT_FILE", args: 1)
cli.v(longOpt: 'verbose', 'verbose output')
options = cli.parse(args)
if (options.h) {
    cli.usage()
    return
}

// set program parameters
reducerOutputGlobalName = options.r ?: REDUCER_OUTPUT_GLOBAL
top = (options.t ?: TOP) as int
latestYearWithData = (options.latestYearWithData ?: LATEST_YEAR_WITH_DATA) as int
maxYearsToGoBackFromLatest = (options.maxYearsToGoBackFromLatest ?: MAX_YEARS_TO_GO_BACK_FROM_LATEST) as int
outputFile = new File((options.outputFile ?: OUTPUT_FILE).replace('"', ''))
verbose = options.v

// start main logic

connection = new ConnectionFactory().createConnection(this /*listener*/)
reducerOutputGlobal = connection.createNodeReference(reducerOutputGlobalName)
if (!reducerOutputGlobal.hasSubnodes()) {
    println "Global $reducerOutputGlobalName is empty"
    return
}
topAvgRankingsGlobal = connection.createNodeReference(TOP_AVG_RANKINGS_GLOBAL)
if (topAvgRankingsGlobal.hasSubnodes()) {
    println "Warning: Global $TOP_AVG_RANKINGS_GLOBAL will be cleared. Hit Enter to continue..."
    System.in.withReader{ it.readLine() }
    topAvgRankingsGlobal.kill()
}
if (outputFile.exists()) {
    println "Warning: File ${outputFile.absolutePath} will be cleared. Hit Enter to continue..."
    System.in.withReader{ it.readLine() }
}

regions = [] // will store subscripts for each region represented in the reducerOutputGlobal

navigator = new NodeNavigator(reducerOutputGlobal, this /*listener*/)
navigator.maxNodeDepth = 4 // 4 region coords: latUp, latDown, lonLeft, lonRight

navigator.navigateNodes() // collect all region subscripts via listener interface

regions.eachWithIndex { region, i ->
    def year = mostRecentYearWithDataIn(region)
    def avg  = averageReadingIn(region, year)

    def avgDuplicateResolver = i // in case different regions have same avg
    def dataForAvgAsCSV = [region.join(','), year].join(',')

    def tooFarBack = yearTooFarBack(year)
    if (verbose)
        println "region ${i+1}\t$dataForAvgAsCSV\t$avg ${tooFarBack ? '\tSKIPPED ' + year + ' too far back' : ''}"

    if (!tooFarBack)
        topAvgRankingsGlobal.set(dataForAvgAsCSV, avg, avgDuplicateResolver)
}

// to get top N, leverage fact that averages now sorted in the global as 1st subscript
regionsWithTopAvgs = [] // will fill this iterating thru topAvgRankingsGlobal
currentAvgSubscript = Double.MAX_VALUE // no average can be higher than this
numAvgsRetrieved = 0
noMoreNodes = false
while (numAvgsRetrieved < top && !noMoreNodes) {
    currentAvgSubscript = topAvgRankingsGlobal.previousSubscript(currentAvgSubscript)
    if (currentAvgSubscript == '') {
        noMoreNodes = true
    } else {
        def avgDuplicateResolver = topAvgRankingsGlobal.nextSubscript(
            PathUtils.path(currentAvgSubscript, ''))
        def dataForAvgAsCSV = topAvgRankingsGlobal.getObject(
            PathUtils.path(currentAvgSubscript, avgDuplicateResolver))
        regionsWithTopAvgs << [dataForAvgAsCSV, currentAvgSubscript].join(',')
        numAvgsRetrieved++
    }
}

outputFile.withPrintWriter { writer ->
    regionsWithTopAvgs.eachWithIndex { regionsWithTopAvg, i ->
        println "rank ${i+1}\t$regionsWithTopAvg"
        writer.println regionsWithTopAvg
    }
}

topAvgRankingsGlobal.kill()
topAvgRankingsGlobal.close()
reducerOutputGlobal.close()
connection.close()

return

// listener methods, invoked by NodeNavigator

def notifyMaxNodeDepthReached(subscripts) {
    regions << subscripts
}

def notifyStatus(message) {
    if (verbose)
        println message
}

def notifyException(e) {
    println "***ERROR*** ${e.class.simpleName}: ${e.localizedMessage}"
}

def notifyLeafNode(subscripts, data) { // ignore
}

// helper methods, invoked by this program

def mostRecentYearWithDataIn(region) {
    def futureYear = Integer.MAX_VALUE
    def mostRecentYear = reducerOutputGlobal.previousSubscript(PathUtils.path(region, futureYear))
    mostRecentYear as int
}

def averageReadingIn(region, year) {
    def data = reducerOutputGlobal.getObject(PathUtils.path(region, year))
    // data is comma separated list of values: count, min, max, average
    // want the last value (average)
    def values = data.split(',')
    values.last() as double
}

def yearTooFarBack(year) {
    year < latestYearWithData - maxYearsToGoBackFromLatest
}
