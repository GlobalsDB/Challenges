import java.text.SimpleDateFormat

import com.intersys.globals.NodeReference

class Mapper extends AbstractRunner {

    private NodeReference global

    Mapper(global) {
        this.global = global
    }

    def run() {
        listener.notifyStatus('mapper started')

        def totalLines = 0
        def dateParser = new SimpleDateFormat(params.dateFormat)

        new File(params.dataSetFile).eachLine { line, lineNumber ->
            if (params.hasHeaderLine && lineNumber == 1)
                return // skip header line

            if (lineNumber % 100000 == 0)
                listener.notifyStatus("processed ${lineNumber} lines")

            mapLine(line, lineNumber, dateParser)
            totalLines ++
        }

        listener.notifyStatus("processed ${totalLines} lines total")
        listener.notifyStatus('mapper complete')
    }

    private def mapLine(line, lineNumber, dateParser) {
        def fields = line.tokenize(params.fieldDelimiter)

        def latitude  = Double.parseDouble(fields[params.latitudeColumn  - 1])
        def longitude = Double.parseDouble(fields[params.longitudeColumn - 1])

        def date  = dateParser.parse(fields[params.dateColumn - 1])
        def year  = date.year + 1900
        def month = date.month + 1

        def measurement = Double.parseDouble(fields[params.measurementColumn - 1])

        def rgn = GeoRegion.computeRegionAroundLatLon(latitude, longitude, params.degreesLatitude, params.degreesLongitude)

        def subscripts = [rgn.latitudeUp, rgn.latitudeDown, rgn.longitudeLeft, rgn.longitudeRight, year]
        if (params.groupByMonth)
            subscripts << month
        subscripts << lineNumber // use lineNumber as duplicate resolver to allow duplicate key values
        subscripts = subscripts.toArray()

        global.set(measurement, subscripts)
    }
}
