import com.intersys.globals.NodeReference

class Reducer extends AbstractRunner {

    private NodeReference mapperGlobal
    private NodeReference reducerGlobal
    private def isNavigatingMapperGlobal // otherwise navigating reducer global
    private def progressCounter

    def verbose

    // intermediate leaf subscript names, ordered according to desired ordering in CSV reduction
    // i.e., want to reduce data in leaf nodes to a CSV list: count, min, max, average
    private static final COUNT_SUBSCRIPT = '1count'
    private static final MIN_SUBSCRIPT = '2min'
    private static final MAX_SUBSCRIPT = '3max'
    private static final TOTAL_SUBSCRIPT = '4total' // used along with count to compute average

    Reducer(mapperGlobal, reducerGlobal) {
        this.mapperGlobal = mapperGlobal
        this.reducerGlobal = reducerGlobal
    }

    def run() {
        try {
            listener.notifyStatus('reducer started')

            isNavigatingMapperGlobal = true
            // first pass - navigate mapper global to build reducer global with intermediate reduction computations
            progressCounter = 0
            def mapperNavigator = new NodeNavigator(mapperGlobal, this)
            mapperNavigator.ignoreLeafNode = true
            mapperNavigator.navigateNodes()
            listener.notifyStatus("processed ${progressCounter} measurements total")

            isNavigatingMapperGlobal = false
            // second pass - navigate reducer global to do final reduction computations (like computing average)
            progressCounter = 0
            def reducerNavigator = new NodeNavigator(reducerGlobal, this)
            reducerNavigator.ignoreLeafNode = false
            reducerNavigator.navigateNodes()
            listener.notifyStatus("reduced measurements to ${progressCounter} groups")

            listener.notifyStatus('reducer complete')
        } catch (e) {
            listener.notifyException(e)
        }
    }

    // listener methods, invoked by NodeNavigator

    def notifyLeafNode(subscripts, measurement) {
        if (isNavigatingMapperGlobal)
            notifyMapperLeafNode(subscripts, measurement)
        else
            notifyReducerLeafNode(subscripts, measurement)
    }

    def notifyMapperLeafNode(subscripts, measurement) {
        def totalSubscriptPath = path(subscripts, TOTAL_SUBSCRIPT)
        def countSubscriptPath = path(subscripts, COUNT_SUBSCRIPT)
        def minSubscriptPath = path(subscripts, MIN_SUBSCRIPT)
        def maxSubscriptPath = path(subscripts, MAX_SUBSCRIPT)

        if (!reducerGlobal.nextSubscript(path(subscripts, ''))) {
            // initialize reduced values

            if (params.computeAverage) {
                reducerGlobal.set(0.0d, totalSubscriptPath)
                reducerGlobal.set(0L, countSubscriptPath)
            }

            if (params.computeMin)
                reducerGlobal.set(Double.MAX_VALUE, minSubscriptPath)

            if (params.computeMax)
                reducerGlobal.set(Double.MIN_VALUE, maxSubscriptPath)
        }

        if (params.computeAverage) {
            def total = reducerGlobal.getDouble(totalSubscriptPath)
            reducerGlobal.set(total + measurement, totalSubscriptPath)

            def count = reducerGlobal.getLong(countSubscriptPath)
            reducerGlobal.set(count + 1, countSubscriptPath)
        }

        if (params.computeMin) {
            def min = reducerGlobal.getDouble(minSubscriptPath)
            if (measurement < min)
                reducerGlobal.set(measurement, minSubscriptPath)
        }

        if (params.computeMax) {
            def max = reducerGlobal.getDouble(maxSubscriptPath)
            if (measurement > max)
                reducerGlobal.set(measurement, maxSubscriptPath)
        }

        progressCounter ++
        if (progressCounter % 100000 == 0)
            listener.notifyStatus("processed ${progressCounter} measurements")
    }

    def notifyReducerLeafNode(subscripts, data) {
        def leafSubscript = subscripts.last()
        switch (leafSubscript) {
            case COUNT_SUBSCRIPT:
            case MIN_SUBSCRIPT:
            case MAX_SUBSCRIPT:
            case TOTAL_SUBSCRIPT:
                // will fold data into parent node value as CSV, so kill leaf node
                reducerGlobal.kill(subscripts)
                break
        }

        def parentSubscripts = subscripts[0..subscripts.size()-2].toArray()

        // recall desired order in CSV list: count, min, max, avg
        switch (leafSubscript) {
        case COUNT_SUBSCRIPT:
        case MIN_SUBSCRIPT:
        case MAX_SUBSCRIPT:
            appendToCSVList(data, parentSubscripts)
            break
        case TOTAL_SUBSCRIPT:
            calcAverageAndAppendToCSVList(data, parentSubscripts)
            break
        }
    }

    private def appendToCSVList(data, subscripts) {
        def valueList = reducerGlobal.getString(subscripts)
        if (!valueList)
            valueList = ''
        if (valueList)
            valueList += ','
        valueList += data
        reducerGlobal.set(valueList, subscripts)
    }

    private def calcAverageAndAppendToCSVList(total, subscripts) {
        def valueList = reducerGlobal.getString(subscripts)
        def values = valueList.tokenize(',')
        def count = Long.parseLong(values[0])
        def average = total / count
        valueList += ','
        valueList += average
        reducerGlobal.set(valueList, subscripts)

        progressCounter ++
        if (verbose)
            listener.notifyStatus("group ${progressCounter}: KEY ${subscripts} VAL [${valueList}]")
    }

    private def path(parentSubscripts, currentSubscript) {
        def pathSubscripts = []
        pathSubscripts.addAll(parentSubscripts)
        pathSubscripts.add(currentSubscript)
        pathSubscripts.toArray()
    }

    def notifyMaxNodeDepthReached(subscripts) {
    }

    def notifyStatus(message) {
        listener.notifyStatus(message)
    }

    def notifyException(e) {
        listener.notifyException(e)
    }
}
